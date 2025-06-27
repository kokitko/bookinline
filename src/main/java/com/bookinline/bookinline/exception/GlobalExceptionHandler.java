package com.bookinline.bookinline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorObject> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<String> errors = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();

        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("Validation failed: " + String.join(", ", errors));
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorObject> handleBookingNotFoundException(BookingNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(FailedRequestParsingException.class)
    public ResponseEntity<ErrorObject> handleFailedRequestParsingException(FailedRequestParsingException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailBeingUsedException.class)
    public ResponseEntity<ErrorObject> handleEmailBeingUsedException(EmailBeingUsedException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(409);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalRoleException.class)
    public ResponseEntity<ErrorObject> handleIllegalRoleException(IllegalRoleException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorObject> handleInvalidUserDataException(InvalidUserDataException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverReviewingException.class)
    public ResponseEntity<ErrorObject> handleOverReviewingException(OverReviewingException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyNotAvailableException.class)
    public ResponseEntity<ErrorObject> handlePropertyNotAvailableException(PropertyNotAvailableException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ErrorObject> handlePropertyNotFoundException(PropertyNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorObject> handleReviewNotFoundException(ReviewNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorObject> handleUnauthorizedActionException(UnauthorizedActionException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(403);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedReviewException.class)
    public ResponseEntity<ErrorObject> handleUnauthorizedReviewException(UnauthorizedReviewException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(403);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFoundException(UserNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject> handleInvalidBookingDatesException(InvalidBookingDatesException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject> handleInvalidPropertyDataException(InvalidPropertyDataException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject> handleUserIsBannedException(UserIsBannedException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(403);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject> handleInvalidRefreshTokenException(InvalidRefreshTokenException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(401);
        errorObject.setMessage(e.getMessage());
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.UNAUTHORIZED);
    }
}
