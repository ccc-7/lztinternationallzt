package edu.bupt.ta.model;

/**
 * Possible states of a job posting.
 * @see Job
 */
public enum JobStatus {
    OPEN,
    CLOSED;

    /**
     * Parses a string value into a JobStatus.
     * Comparison is case-insensitive and trim is applied.
     * Returns {@link #OPEN} if the value is null, blank, or unrecognized.
     *
     * @param value the string representation of the status
     * @return the corresponding JobStatus, or OPEN as a default
     */
    public static JobStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPEN;
        }
        return JobStatus.valueOf(value.trim().toUpperCase());
    }
}