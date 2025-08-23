package com.sellout.exception;

/**
 * Base exception class for SellOut EasyTrack application
 */
public class SellOutException extends RuntimeException {

    public SellOutException(String message) {
        super(message);
    }

    public SellOutException(String message, Throwable cause) {
        super(message, cause);
    }
}