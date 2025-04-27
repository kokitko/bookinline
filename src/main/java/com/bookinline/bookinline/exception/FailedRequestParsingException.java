package com.bookinline.bookinline.exception;

import java.io.Serial;

public class FailedRequestParsingException extends RuntimeException {
  @Serial
    private static final long serialVersionUID = 15L;

    public FailedRequestParsingException(String message) {
      super(message);
    }
}
