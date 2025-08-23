package com.sellout.exception;

/**
 * Exception thrown when import operations fail
 */
public class ImportException extends SellOutException {

    public ImportException(String message) {
        super(message);
    }

    public ImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
