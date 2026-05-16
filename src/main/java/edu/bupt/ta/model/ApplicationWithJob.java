package edu.bupt.ta.model;

/**
 * A read-only view object that combines an {@link Application} with denormalized
 * job details (title, module code, and organiser). Used by JSP pages to display
 * application rows without needing to perform a join. This class is not persisted
 * to CSV; it is assembled at query time.
 */
public class ApplicationWithJob {
    private Application application;
    private String jobTitle;
    private String moduleCode;
    private String organiser;

    /**
     * @param application  the underlying application
     * @param jobTitle    denormalised job title
     * @param moduleCode  denormalised module code
     * @param organiser   denormalised organiser name
     */
    public ApplicationWithJob(Application application, String jobTitle, String moduleCode, String organiser) {
        this.application = application;
        this.jobTitle = jobTitle;
        this.moduleCode = moduleCode;
        this.organiser = organiser;
    }

    /** @return the underlying application */
    public Application getApplication() {
        return application;
    }

    /** @return the application ID */
    public String getApplicationId() {
        return application.getApplicationId();
    }

    /** @return the job ID */
    public String getJobId() {
        return application.getJobId();
    }

    /** @return the status name */
    public String getStatus() {
        return application.getStatus().name();
    }

    /** @return the submission timestamp */
    public String getSubmittedAt() {
        return application.getSubmittedAt();
    }

    /** @return the notes */
    public String getNotes() {
        return application.getNotes();
    }

    /** @return the denormalised job title */
    public String getJobTitle() {
        return jobTitle;
    }

    /** @return the denormalised module code */
    public String getModuleCode() {
        return moduleCode;
    }

    /** @return the denormalised organiser name */
    public String getOrganiser() {
        return organiser;
    }
}
