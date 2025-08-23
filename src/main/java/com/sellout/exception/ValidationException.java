package com.sellout.exception;

/**
 * Exception thrown when data validation fails
 */
public class ValidationException extends SellOutException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}