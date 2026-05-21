package edu.bupt.ta.model;

/**
 * Possible states of a TA's job application.
 * @see Application
 */
public enum ApplicationStatus {

    /** Application submitted and awaiting review. */
    PENDING,

    /** Application shortlisted; an interview is scheduled. */
    INTERVIEW,

    /** Application approved; TA has been selected. */
    ACCEPTED,

    /** Application declined or rejected. */
    REJECTED,

    /** Application withdrawn by the TA applicant. */
    WITHDRAWN;

    /**
     * Parses a string value into an ApplicationStatus.
     * Comparison is case-insensitive and trim is applied.
     * Returns {@link #PENDING} if the value is null, blank, or unrecognized.
     *
     * @param value the string representation of the status
     * @return the corresponding ApplicationStatus, or PENDING as a default
     */
    public static ApplicationStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        return ApplicationStatus.valueOf(value.trim().toUpperCase());
    }
}
