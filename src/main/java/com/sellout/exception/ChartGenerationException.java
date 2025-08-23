package com.sellout.exception;

/**
 * Exception thrown when chart generation fails
 */
public class ChartGenerationException extends SellOutException {

    public ChartGenerationException(String message) {
        super(message);
    }

    public ChartGenerationException(String message, Throwable cause) {
        super(message, cause);
    }
}