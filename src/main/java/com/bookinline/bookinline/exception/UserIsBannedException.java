package com.bookinline.bookinline.exception;

import java.io.Serial;

public class UserIsBannedException extends RuntimeException {
  @Serial
  public static final long serialVersionUID = 14L;

  public UserIsBannedException(String message) {
    super(message);
  }
}
