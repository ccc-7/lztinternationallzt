package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.model.Job;
import edu.bupt.ta.model.JobStatus;
import edu.bupt.ta.model.User;
import edu.bupt.ta.storage.FileStorageUtil;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Provides business-logic operations for job applications.
 * Handles TA application submission, status transitions, and application queries.
 */
public class ApplicationService {

    /** Maximum number of active (PENDING or INTERVIEW) applications a single TA can hold. */
    private static final int MAX_APPLICATIONS_PER_TA = 3;

    private final FileStorageUtil storage = new FileStorageUtil();
    private final UserService userService = new UserService();
    private final JobService jobService = new JobService();

    /**
     * Submits a new job application for a TA user.
     * Performs multiple validations before accepting the application:
     * user exists, user is application-ready, job exists, job is OPEN,
     * user's year is within [minYear, maxYear], deadline not passed,
     * vacancy not exceeded, no duplicate application, and active-app limit not exceeded.
     *
     * @param userId the applying TA's user ID
     * @param jobId  the target job ID
     * @return the newly created Application record
     * @throws IllegalArgumentException if any validation check fails
     */
    public Application apply(String userId, String jobId) {
        User user = userService.findById(userId);
        if (user == null) {
            throw new IllegalArgumentException("user not found.");
        }
        if (!userService.isApplicationReady(user)) {
            throw new IllegalArgumentException("Please complete your candidate summary or upload a PDF CV before applying.");
        }

        Job job = jobService.findById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("job not found.");
        }
        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalArgumentException("This job is not open for applications.");
        }
        if (job.getMinYear() > 0 && user.getYear() > 0 && user.getYear() < job.getMinYear()) {
            throw new IllegalArgumentException("Your current year does not meet the minimum requirement for this job.");
        }
        if (job.getMaxYear() > 0 && user.getYear() > 0 && user.getYear() > job.getMaxYear()) {
            throw new IllegalArgumentException("Your current year exceeds the allowed range for this job.");
        }
        if (isDeadlinePassed(job.getDeadline())) {
            throw new IllegalArgumentException("The application deadline for this job has passed.");
        }
        if (job.getVacancies() > 0 && countAcceptedApplications(jobId) >= job.getVacancies()) {
            throw new IllegalArgumentException("This job has reached its vacancy limit.");
        }

        List<Application> applications = storage.loadApplications();

        for (Application app : applications) {
            if (app.getUserId().equals(userId) && app.getJobId().equals(jobId)) {
                throw new IllegalArgumentException("you have already applied for this job.");
            }
        }

        int activeApplicationCount = countUserPendingAndInterview(userId);
        if (activeApplicationCount >= MAX_APPLICATIONS_PER_TA) {
            throw new IllegalArgumentException("You can only have up to " + MAX_APPLICATIONS_PER_TA + " active applications at a time.");
        }

        Application application = new Application();
        application.setApplicationId(nextApplicationId(applications));
        application.setUserId(userId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmittedAt(storage.nowText());
        application.setNotes("Submitted from TA portal");
        application.setAvailability(user.getAvailability());

        applications.add(application);
        storage.saveApplications(applications);
        return application;
    }

    /** Returns all applications from the CSV. */
    public List<Application> getAllApplications() {
        return storage.loadApplications();
    }

    /** Looks up an application by its ID. Returns null if not found. */
    public Application findById(String applicationId) {
        return storage.loadApplications().stream()
                .filter(app -> app.getApplicationId().equals(applicationId))
                .findFirst()
                .orElse(null);
    }

    /** Returns all applications submitted by the given user. */
    public List<Application> getApplicationsByUserId(String userId) {
        List<Application> result = new ArrayList<>();
        for (Application app : storage.loadApplications()) {
            if (app.getUserId().equals(userId)) {
                result.add(app);
            }
        }
        return result;
    }

    /** Returns all applications for the given job. */
    public List<Application> getApplicationsByJobId(String jobId) {
        List<Application> result = new ArrayList<>();
        for (Application app : storage.loadApplications()) {
            if (app.getJobId().equals(jobId)) {
                result.add(app);
            }
        }
        return result;
    }

    /** Returns all applications whose jobId is in the given set. */
    public List<Application> getApplicationsByJobIds(Set<String> jobIds) {
        List<Application> result = new ArrayList<>();
        if (jobIds == null || jobIds.isEmpty()) {
            return result;
        }

        for (Application app : storage.loadApplications()) {
            if (jobIds.contains(app.getJobId())) {
                result.add(app);
            }
        }
        return result;
    }

    /** Counts how many applications reference the given job. */
    public int countApplicationsByJobId(String jobId) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getJobId().equals(jobId)) {
                count++;
            }
        }
        return count;
    }

    /** Counts how many applications reference any job in the given set. */
    public int countApplicationsByJobIds(Set<String> jobIds) {
        if (jobIds == null || jobIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (jobIds.contains(app.getJobId())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Counts applications for any job in the given set that have the specified status.
     *
     * @param jobIds the set of job IDs to consider
     * @param status the status to match
     * @return the count of matching applications
     */
    public int countApplicationsByJobIdsAndStatus(Set<String> jobIds, ApplicationStatus status) {
        if (jobIds == null || jobIds.isEmpty()) {
            return 0;
        }

        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (jobIds.contains(app.getJobId()) && app.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    /** Returns the total number of applications in the CSV. */
    public int countTotalApplications() {
        return storage.loadApplications().size();
    }

    /**
     * Updates the status of an application, using a default note based on the new status.
     *
     * @param applicationId the application to update
     * @param newStatus    the new status
     * @throws IllegalArgumentException if the application is not found
     */
    public void updateStatus(String applicationId, ApplicationStatus newStatus) {
        updateStatus(applicationId, newStatus, null);
    }

    /**
     * Updates the status and optionally the notes of an application.
     * Sets a default note based on the new status if no override is provided.
     *
     * @param applicationId  the application to update
     * @param newStatus     the new status
     * @param notesOverride optional note to replace the default; if null, a default is set
     * @throws IllegalArgumentException if the application is not found
     */
    public void updateStatus(String applicationId, ApplicationStatus newStatus, String notesOverride) {
        List<Application> applications = storage.loadApplications();
        boolean found = false;

        for (Application app : applications) {
            if (app.getApplicationId().equals(applicationId)) {
                app.setStatus(newStatus);
                if (notesOverride != null && !notesOverride.isBlank()) {
                    app.setNotes(notesOverride);
                } else if (newStatus == ApplicationStatus.ACCEPTED) {
                    app.setNotes("Accepted");
                } else if (newStatus == ApplicationStatus.REJECTED) {
                    app.setNotes("Rejected");
                } else if (newStatus == ApplicationStatus.INTERVIEW) {
                    app.setNotes("Moved to interview");
                }
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("Application not found");
        }

        storage.saveApplications(applications);
    }

    /**
     * Removes all applications whose IDs are in the given list from the CSV.
     *
     * @param applicationIds the IDs to delete
     */
    public void deleteApplications(List<String> applicationIds) {
        List<Application> applications = storage.loadApplications();
        applications.removeIf(a -> applicationIds.contains(a.getApplicationId()));
        storage.saveApplications(applications);
    }

    /**
     * Counts the number of active (PENDING or INTERVIEW) applications for a user.
     * Used to enforce the per-TA application limit.
     */
    public int countUserPendingAndInterview(String userId) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getUserId().equals(userId)
                    && (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.INTERVIEW)) {
                count++;
            }
        }
        return count;
    }

    /** Counts all applications with the given status. */
    public int countAllByStatus(ApplicationStatus status) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

    /** Counts applications for the given job that have been ACCEPTED. */
    private int countAcceptedApplications(String jobId) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getJobId().equals(jobId) && app.getStatus() == ApplicationStatus.ACCEPTED) {
                count++;
            }
        }
        return count;
    }

    /**
     * Checks whether the application deadline has passed. Returns false if deadline
     * is null or blank, or if parsing fails.
     *
     * @param deadline the deadline string in "yyyy-MM-dd" format
     * @return true if the deadline is before today
     */
    private boolean isDeadlinePassed(String deadline) {
        if (deadline == null || deadline.isBlank()) {
            return false;
        }
        try {
            return LocalDate.parse(deadline.trim()).isBefore(LocalDate.now());
        } catch (Exception e) {
            return false;
        }
    }

    /** Generates the next sequential application ID (e.g. "A007"). */
    private String nextApplicationId(List<Application> applications) {
        int max = applications.stream()
                .map(Application::getApplicationId)
                .filter(id -> id != null && id.startsWith("A"))
                .map(id -> {
                    try {
                        return Integer.parseInt(id.substring(1));
                    } catch (Exception e) {
                        return 0;
                    }
                })
                .max(Comparator.naturalOrder())
                .orElse(0);
        return String.format("A%03d", max + 1);
    }
}
