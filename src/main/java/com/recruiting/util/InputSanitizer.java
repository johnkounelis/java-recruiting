package com.recruiting.util;

/**
 * Utility class for sanitizing user input to prevent XSS and other injection
 * attacks.
 */
public class InputSanitizer {

    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_COMPANY_LENGTH = 200;
    private static final int MAX_LOCATION_LENGTH = 200;
    private static final int MAX_NAME_LENGTH = 100;
    private static final int MAX_EMAIL_LENGTH = 255;
    private static final int MAX_NOTES_LENGTH = 2000;
    private static final int MAX_GENERIC_LENGTH = 500;

    /**
     * Strips HTML tags and trims the input string.
     */
    public static String stripHtml(String input) {
        if (input == null)
            return null;
        // Remove HTML tags
        String sanitized = input.replaceAll("<[^>]*>", "");
        // Remove javascript: protocol references
        sanitized = sanitized.replaceAll("(?i)javascript\\s*:", "");
        // Remove on* event handlers
        sanitized = sanitized.replaceAll("(?i)on\\w+\\s*=", "");
        return sanitized.trim();
    }

    /**
     * Sanitize and enforce max length.
     */
    public static String sanitize(String input, int maxLength) {
        if (input == null)
            return null;
        String cleaned = stripHtml(input);
        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        return cleaned;
    }

    public static String sanitizeTitle(String input) {
        return sanitize(input, MAX_TITLE_LENGTH);
    }

    public static String sanitizeDescription(String input) {
        return sanitize(input, MAX_DESCRIPTION_LENGTH);
    }

    public static String sanitizeCompany(String input) {
        return sanitize(input, MAX_COMPANY_LENGTH);
    }

    public static String sanitizeLocation(String input) {
        return sanitize(input, MAX_LOCATION_LENGTH);
    }

    public static String sanitizeName(String input) {
        return sanitize(input, MAX_NAME_LENGTH);
    }

    public static String sanitizeEmail(String input) {
        return sanitize(input, MAX_EMAIL_LENGTH);
    }

    public static String sanitizeNotes(String input) {
        return sanitize(input, MAX_NOTES_LENGTH);
    }

    public static String sanitizeGeneric(String input) {
        return sanitize(input, MAX_GENERIC_LENGTH);
    }

    /**
     * Validates that an email address follows a basic pattern.
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty())
            return false;
        return email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    }
}
