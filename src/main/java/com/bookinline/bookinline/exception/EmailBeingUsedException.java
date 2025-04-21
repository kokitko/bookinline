package com.bookinline.bookinline.exception;

import java.io.Serial;

public class EmailBeingUsedException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 2L;

    public EmailBeingUsedException(String message) {
        super(message);
    }
}
