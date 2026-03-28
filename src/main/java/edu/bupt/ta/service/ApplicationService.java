package edu.bupt.ta.service;

import edu.bupt.ta.model.Application;
import edu.bupt.ta.model.ApplicationStatus;
import edu.bupt.ta.storage.FileStorageUtil;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ApplicationService {

    private final FileStorageUtil storage = new FileStorageUtil();

    public Application apply(String userId, String jobId) {
        List<Application> applications = storage.loadApplications();

        for (Application app : applications) {
            if (app.getUserId().equals(userId) && app.getJobId().equals(jobId)) {
                throw new IllegalArgumentException("你已经申请过这个岗位了");
            }
        }

        Application application = new Application();
        application.setApplicationId(nextApplicationId(applications));
        application.setUserId(userId);
        application.setJobId(jobId);
        application.setStatus(ApplicationStatus.PENDING);
        application.setSubmittedAt(storage.nowText());
        application.setNotes("Submitted from TA portal");

        applications.add(application);
        storage.saveApplications(applications);
        return application;
    }

    public List<Application> getAllApplications() {
        return storage.loadApplications();
    }

    public List<Application> getApplicationsByUserId(String userId) {
        List<Application> result = new ArrayList<>();
        for (Application app : storage.loadApplications()) {
            if (app.getUserId().equals(userId)) {
                result.add(app);
            }
        }
        return result;
    }

    public void updateStatus(String applicationId, ApplicationStatus newStatus) {
        List<Application> applications = storage.loadApplications();
        boolean found = false;

        for (Application app : applications) {
            if (app.getApplicationId().equals(applicationId)) {
                app.setStatus(newStatus);
                if (newStatus == ApplicationStatus.ACCEPTED) {
                    app.setNotes("Accepted by MO");
                } else if (newStatus == ApplicationStatus.REJECTED) {
                    app.setNotes("Rejected by MO");
                } else if (newStatus == ApplicationStatus.INTERVIEW) {
                    app.setNotes("Moved to interview");
                }
                found = true;
                break;
            }
        }

        if (!found) {
            throw new IllegalArgumentException("未找到对应申请记录");
        }

        storage.saveApplications(applications);
    }

    public int countUserPendingAndInterview(String userId) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getUserId().equals(userId) &&
                    (app.getStatus() == ApplicationStatus.PENDING || app.getStatus() == ApplicationStatus.INTERVIEW)) {
                count++;
            }
        }
        return count;
    }

    public int countAllByStatus(ApplicationStatus status) {
        int count = 0;
        for (Application app : storage.loadApplications()) {
            if (app.getStatus() == status) {
                count++;
            }
        }
        return count;
    }

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