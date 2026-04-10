package com.bakery.reports;

/**
 * Wrapper exception for report generation failures.
 * Using RuntimeException keeps controller code simple while still preserving the root cause.
 */
public final class ReportException extends RuntimeException {
    public ReportException(String message, Throwable cause) {
        super(message, cause);
    }
}

