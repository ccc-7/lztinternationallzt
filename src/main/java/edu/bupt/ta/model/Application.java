package edu.bupt.ta.model;

/**
 * Represents a TA's application for a specific job posting.
 * Tracks the application status, submission timestamp, and the applicant's
 * availability at the time of submission. Maps to {@code applications.csv}.
 */
public class Application {
    private String applicationId;
    private String userId;
    private String jobId;
    private ApplicationStatus status;
    private String submittedAt;
    private String notes;
    private String availability;

    public Application() {
    }

    public Application(String applicationId, String userId, String jobId,
                       ApplicationStatus status, String submittedAt, String notes, String availability) {
        this.applicationId = applicationId;
        this.userId = userId;
        this.jobId = jobId;
        this.status = status;
        this.submittedAt = submittedAt;
        this.notes = notes;
        this.availability = availability;
    }

    public String getApplicationId() {
        return applicationId;
    }

    /** @param applicationId the application ID to set */
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    /** @param userId the user ID to set */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    /** @param jobId the job ID to set */
    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    /** @param status the application status to set */
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    /** @param submittedAt the submission timestamp to set */
    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getNotes() {
        return notes;
    }

    /** @param notes the notes to set */
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAvailability() {
        return availability;
    }

    /** @param availability the availability to set */
    public void setAvailability(String availability) {
        this.availability = availability;
    }
}
