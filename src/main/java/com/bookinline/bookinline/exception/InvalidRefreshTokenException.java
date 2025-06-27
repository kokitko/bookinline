package com.bookinline.bookinline.exception;

public class InvalidRefreshTokenException extends RuntimeException {
    private static final long serialVersionUID = 16L;
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
