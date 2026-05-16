package edu.bupt.ta.model;

/**
 * User roles in the TA Recruitment System.
 * TA: Teaching Assistant (applies for jobs)
 * MO:  Module Organiser (posts and manages jobs)
 * ADMIN: System Administrator (full system access)
 */
public enum UserRole {
    TA,
    MO,
    ADMIN;

    /**
     * Parses a string value into a UserRole.
     * Comparison is case-insensitive and trim is applied.
     * Returns {@link #TA} if the value is null, blank, or unrecognized.
     *
     * @param value the string representation of the role
     * @return the corresponding UserRole, or TA as a default
     */
    public static UserRole fromString(String value) {
        if (value == null || value.isBlank()) {
            return TA;
        }
        return UserRole.valueOf(value.trim().toUpperCase());
    }
}