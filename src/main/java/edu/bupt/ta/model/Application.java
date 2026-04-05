package edu.bupt.ta.model;

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

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(String submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }
}