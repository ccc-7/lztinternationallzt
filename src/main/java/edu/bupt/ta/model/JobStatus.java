package edu.bupt.ta.model;

public enum JobStatus {
    OPEN,
    CLOSED;

    public static JobStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return OPEN;
        }
        return JobStatus.valueOf(value.trim().toUpperCase());
    }
}