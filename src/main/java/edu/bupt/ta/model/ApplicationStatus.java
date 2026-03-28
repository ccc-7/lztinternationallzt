package edu.bupt.ta.model;

public enum ApplicationStatus {
    PENDING,
    INTERVIEW,
    ACCEPTED,
    REJECTED;

    public static ApplicationStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            return PENDING;
        }
        return ApplicationStatus.valueOf(value.trim().toUpperCase());
    }
}