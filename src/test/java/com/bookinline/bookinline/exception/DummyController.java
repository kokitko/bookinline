package com.bookinline.bookinline.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dummy")
public class DummyController {
    @GetMapping("/method-argument-not-valid")
    public void throwMethodArgumentNotValidException(@Valid @RequestBody DummyRequest request) {
    }

    public static class DummyRequest {
        @NotBlank(message = "Name cannot be blank")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    @GetMapping("/booking-not-found")
    public void throwBookingNotFoundException() {
        throw new BookingNotFoundException("Booking not found");
    }

    @GetMapping("/email-being-used")
    public void throwEmailBeingUsedException() {
        throw new EmailBeingUsedException("Email is already being used");
    }

    @GetMapping("/failed-request-parsing")
    public void throwFailedRequestParsingException() {
        throw new FailedRequestParsingException("Failed to parse request");
    }

    @GetMapping("/illegal-role")
    public void throwIllegalRoleException() {
        throw new IllegalRoleException("Illegal role");
    }

    @GetMapping("/invalid-user-data")
    public void throwInvalidUserDataException() {
        throw new InvalidUserDataException("Invalid user data");
    }

    @GetMapping("/over-reviewing")
    public void throwOverReviewingException() {
        throw new OverReviewingException("Over reviewing limit reached");
    }

    @GetMapping("/property-not-available")
    public void throwPropertyNotAvailableException() {
        throw new PropertyNotAvailableException("Property not available");
    }

    @GetMapping("/property-not-found")
    public void throwPropertyNotFoundException() {
        throw new PropertyNotFoundException("Property not found");
    }

    @GetMapping("/review-not-found")
    public void throwReviewNotFoundException() {
        throw new ReviewNotFoundException("Review not found");
    }

    @GetMapping("/unauthorized-action")
    public void throwUnauthorizedActionException() {
        throw new UnauthorizedActionException("Unauthorized action");
    }

    @GetMapping("/unauthorized-review")
    public void throwUnauthorizedReviewException() {
        throw new UnauthorizedReviewException("Unauthorized review");
    }

    @GetMapping("/user-not-found")
    public void throwUserNotFoundException() {
        throw new UserNotFoundException("User not found");
    }

    @GetMapping("/invalid-booking-dates")
    public void throwInvalidBookingDatesException() {
        throw new InvalidBookingDatesException("Invalid booking dates");
    }

    @GetMapping("/invalid-property-data")
    public void throwInvalidPropertyDataException() {
        throw new InvalidPropertyDataException("Invalid property data");
    }

    @GetMapping("/user-is-banned")
    public void throwUserIsBannedException() {
        throw new UserIsBannedException("User is banned");
    }
}
