package com.bookinline.bookinline.exception;

import java.io.Serial;

public class InvalidUserDataException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 4L;

    public InvalidUserDataException(String message) {
        super(message);
    }
}
