package com.bookinline.bookinline.exception;

import java.io.Serial;

public class ReviewNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 10L;

    public ReviewNotFoundException(String message) {
        super(message);
    }
}
