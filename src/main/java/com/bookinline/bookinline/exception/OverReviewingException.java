package com.bookinline.bookinline.exception;

import java.io.Serial;

public class OverReviewingException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 9L;

    public OverReviewingException(String message) {
        super(message);
    }
}
