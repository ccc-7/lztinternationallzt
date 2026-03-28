package edu.bupt.ta.model;

public enum UserRole {
    TA,
    MO,
    ADMIN;

    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            return TA;
        }
        return UserRole.valueOf(value.trim().toUpperCase());
    }
}