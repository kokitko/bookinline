package com.bookinline.bookinline.exception;

import java.io.Serial;

public class UnauthorizedReviewException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 8L;

    public UnauthorizedReviewException(String message) {
        super(message);
    }
}
