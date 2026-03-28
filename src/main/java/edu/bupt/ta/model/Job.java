package edu.bupt.ta.model;

public class Job {
    private String jobId;
    private String title;
    private String moduleCode;
    private String organiser;
    private int minYear;
    private int maxYear;
    private int hours;
    private JobStatus status;
    private String requiredSkills;
    private int matchScore;

    public Job() {
    }

    public Job(String jobId, String title, String moduleCode, String organiser,
               int minYear, int maxYear, int hours, JobStatus status,
               String requiredSkills, int matchScore) {
        this.jobId = jobId;
        this.title = title;
        this.moduleCode = moduleCode;
        this.organiser = organiser;
        this.minYear = minYear;
        this.maxYear = maxYear;
        this.hours = hours;
        this.status = status;
        this.requiredSkills = requiredSkills;
        this.matchScore = matchScore;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getModuleCode() {
        return moduleCode;
    }

    public void setModuleCode(String moduleCode) {
        this.moduleCode = moduleCode;
    }

    public String getOrganiser() {
        return organiser;
    }

    public void setOrganiser(String organiser) {
        this.organiser = organiser;
    }

    public int getMinYear() {
        return minYear;
    }

    public void setMinYear(int minYear) {
        this.minYear = minYear;
    }

    public int getMaxYear() {
        return maxYear;
    }

    public void setMaxYear(int maxYear) {
        this.maxYear = maxYear;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public int getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(int matchScore) {
        this.matchScore = matchScore;
    }
}