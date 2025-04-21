package com.bookinline.bookinline.exception;

import java.io.Serial;

public class IllegalRoleException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 3L;

    public IllegalRoleException(String message) {
        super(message);
    }
}
