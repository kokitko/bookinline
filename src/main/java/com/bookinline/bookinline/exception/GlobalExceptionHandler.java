package com.bookinline.bookinline.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorObject> handleBookingNotFoundException(BookingNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage("Booking not found");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EmailBeingUsedException.class)
    public ResponseEntity<ErrorObject> handleEmailBeingUsedException(EmailBeingUsedException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(409);
        errorObject.setMessage("Email is already being used");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(IllegalRoleException.class)
    public ResponseEntity<ErrorObject> handleIllegalRoleException(IllegalRoleException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("Illegal role");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidUserDataException.class)
    public ResponseEntity<ErrorObject> handleInvalidUserDataException(InvalidUserDataException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("Invalid user data");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OverReviewingException.class)
    public ResponseEntity<ErrorObject> handleOverReviewingException(OverReviewingException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("User can review certain property only once");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyNotAvailableException.class)
    public ResponseEntity<ErrorObject> handlePropertyNotAvailableException(PropertyNotAvailableException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("Property is not available");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PropertyNotFoundException.class)
    public ResponseEntity<ErrorObject> handlePropertyNotFoundException(PropertyNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage("Property not found");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorObject> handleReviewNotFoundException(ReviewNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage("Review not found");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedActionException.class)
    public ResponseEntity<ErrorObject> handleUnauthorizedActionException(UnauthorizedActionException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(403);
        errorObject.setMessage("Unauthorized action");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UnauthorizedReviewException.class)
    public ResponseEntity<ErrorObject> handleUnauthorizedReviewException(UnauthorizedReviewException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(403);
        errorObject.setMessage("User must visit the property to review it");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorObject> handleUserNotFoundException(UserNotFoundException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(404);
        errorObject.setMessage("User not found");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorObject> handleInvalidBookingDatesException(InvalidBookingDatesException e) {
        ErrorObject errorObject = new ErrorObject();

        errorObject.setStatusCode(400);
        errorObject.setMessage("Invalid booking dates");
        errorObject.setTimestamp(new Date());

        return new ResponseEntity<>(errorObject, HttpStatus.BAD_REQUEST);
    }
}
