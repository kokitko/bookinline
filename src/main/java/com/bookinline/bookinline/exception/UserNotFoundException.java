package com.bookinline.bookinline.exception;

import java.io.Serial;

public class UserNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 7L;

    public UserNotFoundException(String message) {
        super(message);
    }
}
