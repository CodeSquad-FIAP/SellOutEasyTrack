package com.sellout.exception;

/**
 * Exception thrown when database operations fail
 */
public class DatabaseException extends SellOutException {

    public DatabaseException(String message) {
        super(message);
    }

    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}