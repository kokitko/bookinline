package com.bookinline.bookinline.exception;

import java.io.Serial;

public class TokenIsExpiredException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 17L;

    public TokenIsExpiredException(String message) {
        super(message);
    }
}
