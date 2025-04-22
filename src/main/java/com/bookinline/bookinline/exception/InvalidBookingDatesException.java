package com.bookinline.bookinline.exception;

import java.io.Serial;

public class InvalidBookingDatesException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 12L;
    public InvalidBookingDatesException(String message) {
        super(message);
    }
}
