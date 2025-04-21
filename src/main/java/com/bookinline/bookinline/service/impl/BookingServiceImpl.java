package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.BookingStatus;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.UserRepository;
import com.bookinline.bookinline.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              PropertyRepository propertyRepository,
                              UserRepository userRepository) {
        this.bookingRepository = bookingRepository;
        this.propertyRepository = propertyRepository;
        this.userRepository = userRepository;
    }

    @Override
    public BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto, Long propertyId, Long userId) {
        logger.info("Attempting to book property with ID: {} for user with ID: {}", propertyId, userId);
        if (!isPropertyAvailable(propertyId, bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate())) {
            logger.warn("Property with ID: {} is not available for date range: {} to {}",
                    propertyId, bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());
            throw new PropertyNotAvailableException("Property is not available for this date range");
        }
        Booking booking = mapToBookingEntity(bookingRequestDto, propertyId, userId);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking has been saved successfully with id: {}", savedBooking.getId());
        return mapToBookingResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto cancelBooking(Long bookingId, Long userId) {
        logger.info("Attempting to cancel booking with ID: {} for user ID: {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking with ID: {} not found", bookingId);
                    return new BookingNotFoundException("Booking not found");
                });
        User guest = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID: {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        if (!booking.getGuest().getId().equals(guest.getId())) {
            logger.warn("Unauthorized action: User with ID: {} is not the guest of booking with ID: {}", userId, bookingId);
            throw new UnauthorizedActionException("You are not able to cancel this booking");
        }
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking with ID: {} has been cancelled successfully", bookingId);
        return mapToBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponsePage getBookingsByUserId(Long userId, int page, int size) {
        logger.info("Fetching booking for user ID: {}, page: {}, size: {}", userId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookingPage = bookingRepository.findByGuestId(userId, pageable);
        logger.info("Found {} bookings for user ID: {}", bookingPage.getTotalElements(), userId);
        List<BookingResponseDto> bookingResponseDtos = bookingPage.getContent()
                .stream()
                .map(this::mapToBookingResponseDto).toList();

        BookingResponsePage bookingResponsePage = new BookingResponsePage();
        bookingResponsePage.setPage(bookingPage.getNumber());
        bookingResponsePage.setSize(bookingPage.getSize());
        bookingResponsePage.setTotalElements(bookingPage.getTotalElements());
        bookingResponsePage.setTotalPages(bookingPage.getTotalPages());
        bookingResponsePage.setLast(bookingPage.isLast());
        bookingResponsePage.setBookings(bookingResponseDtos);
        return bookingResponsePage;
    }

    @Override
    public BookingResponsePage getBookingsByPropertyId(Long propertyId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookingPage = bookingRepository.findByPropertyId(propertyId, pageable);
        List<BookingResponseDto> bookingResponseDtos = bookingPage.getContent()
                .stream()
                .map(this::mapToBookingResponseDto).toList();

        BookingResponsePage bookingResponsePage = new BookingResponsePage();
        bookingResponsePage.setPage(bookingPage.getNumber());
        bookingResponsePage.setSize(bookingPage.getSize());
        bookingResponsePage.setTotalElements(bookingPage.getTotalElements());
        bookingResponsePage.setTotalPages(bookingPage.getTotalPages());
        bookingResponsePage.setLast(bookingPage.isLast());
        bookingResponsePage.setBookings(bookingResponseDtos);
        return bookingResponsePage;
    }

    @Override
    public BookingResponseDto confirmBooking(Long bookingId, Long userId) {
        logger.info("Attempting to confirm booking with ID: {} for user ID: {}", bookingId, userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> {
                    logger.error("Booking with ID: {} not found", bookingId);
                    return new BookingNotFoundException("Booking not found");
                });
        User host = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID: {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        if (!booking.getProperty().getHost().getId().equals(host.getId())) {
            logger.warn("Unauthorized action: User with ID: {} is not the host of booking with ID: {}", userId, bookingId);
            throw new UnauthorizedActionException("You are not able to confirm this booking");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking with ID: {} successfully confirmed", bookingId);
        return mapToBookingResponseDto(updatedBooking);
    }

    private boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.info("Checking availability for property ID: {} from {} to {}", propertyId, startDate, endDate);
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId, Pageable.unpaged()).getContent();
        for (Booking booking : bookings) {
            if (!(startDate.isAfter(booking.getCheckOutDate()) || startDate.isEqual(booking.getCheckOutDate())) &&
                    !(endDate.isBefore(booking.getCheckInDate()) || endDate.isEqual(booking.getCheckInDate()))) {
                return false;
            }
        }
        logger.info("Property ID: {} is available from {} to {}", propertyId, startDate, endDate);
        return true;
    }

    private BookingResponseDto mapToBookingResponseDto(Booking booking) {
        return BookingResponseDto.builder()
                .id(booking.getId())
                .checkInDate(booking.getCheckInDate())
                .checkOutDate(booking.getCheckOutDate())
                .guestName(booking.getGuest().getFullName())
                .propertyTitle(booking.getProperty().getTitle())
                .status(String.valueOf(booking.getStatus()))
                .build();
    }

    private Booking mapToBookingEntity(BookingRequestDto bookingRequestDto, Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));
        User guest = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return Booking.builder()
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .property(property)
                .guest(guest)
                .status(BookingStatus.PENDING)
                .build();
    }
}
