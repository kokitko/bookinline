package com.bookinline.bookinline.exception;

public class PropertyNotAvailableException extends RuntimeException {
    private static final long serialVersionUID = 5L;

    public PropertyNotAvailableException(String message) {
        super(message);
    }
}
