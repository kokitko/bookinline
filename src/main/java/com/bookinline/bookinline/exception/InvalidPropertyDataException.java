package com.bookinline.bookinline.exception;

import java.io.Serial;

public class InvalidPropertyDataException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 13L;

    public InvalidPropertyDataException(String message) {
        super(message);
    }
}
