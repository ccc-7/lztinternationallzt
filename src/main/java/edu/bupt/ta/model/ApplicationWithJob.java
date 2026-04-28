package edu.bupt.ta.model;

public class ApplicationWithJob {
    private Application application;
    private String jobTitle;
    private String moduleCode;
    private String organiser;

    public ApplicationWithJob(Application application, String jobTitle, String moduleCode, String organiser) {
        this.application = application;
        this.jobTitle = jobTitle;
        this.moduleCode = moduleCode;
        this.organiser = organiser;
    }

    public Application getApplication() {
        return application;
    }

    public String getApplicationId() {
        return application.getApplicationId();
    }

    public String getJobId() {
        return application.getJobId();
    }

    public String getStatus() {
        return application.getStatus().name();
    }

    public String getSubmittedAt() {
        return application.getSubmittedAt();
    }

    public String getNotes() {
        return application.getNotes();
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public String getOrganiser() {
        return organiser;
    }
}
