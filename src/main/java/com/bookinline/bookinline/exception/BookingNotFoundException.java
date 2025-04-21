package com.bookinline.bookinline.exception;

import java.io.Serial;

public class BookingNotFoundException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 11L;

    public BookingNotFoundException(String message) {
        super(message);
    }
}
