package com.bookinline.bookinline.exception;

import java.io.Serial;

public class PropertyNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 6L;

    public PropertyNotFoundException(String message) {
        super(message);
    }
}
