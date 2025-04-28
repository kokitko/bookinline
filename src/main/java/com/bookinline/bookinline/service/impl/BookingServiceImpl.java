package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.BookingDatesDto;
import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.exception.*;
import com.bookinline.bookinline.mapper.BookingMapper;
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
        if (bookingRequestDto.getCheckInDate().isAfter(bookingRequestDto.getCheckOutDate()) ||
            bookingRequestDto.getCheckInDate().isEqual(bookingRequestDto.getCheckOutDate())) {
            logger.warn("Invalid booking dates: Check-in date is after or same as check-out date");
            throw new InvalidBookingDatesException("Check-in date must be before check-out date");
        }
        if (!isPropertyAvailable(propertyId, bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate())) {
            logger.warn("Property with ID: {} is not available for date range: {} to {}",
                    propertyId, bookingRequestDto.getCheckInDate(), bookingRequestDto.getCheckOutDate());
            throw new PropertyNotAvailableException("Property is not available for this date range");
        }
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    logger.error("Property with ID: {} not found", propertyId);
                    return new PropertyNotFoundException("Property not found");
                });
        User guest = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.error("User with ID: {} not found", userId);
                    return new UserNotFoundException("User not found");
                });
        Booking booking = BookingMapper.mapToBookingEntity(bookingRequestDto, property, guest);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking has been saved successfully with id: {}", savedBooking.getId());
        return BookingMapper.mapToBookingResponseDto(savedBooking);
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
        return BookingMapper.mapToBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponseDto getBookingById(Long bookingId, Long userId) {
        logger.info("Fetching booking with ID: {} for user ID: {}", bookingId, userId);
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
            throw new UnauthorizedActionException("You are not able to view this booking");
        }
        logger.info("Booking with ID: {} fetched successfully", bookingId);
        return BookingMapper.mapToBookingResponseDto(booking);
    }

    @Override
    public BookingResponsePage getBookingsByUserId(Long userId, int page, int size) {
        logger.info("Fetching booking for user ID: {}, page: {}, size: {}", userId, page, size);
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookingPage = bookingRepository.findByGuestId(userId, pageable);
        logger.info("Found {} bookings for user ID: {}", bookingPage.getTotalElements(), userId);
        List<BookingResponseDto> bookingResponseDtos = bookingPage.getContent()
                .stream()
                .map(BookingMapper::mapToBookingResponseDto).toList();

        return BookingMapper.mapToBookingResponsePage(bookingPage, bookingResponseDtos);
    }

    @Override
    public BookingResponsePage getBookingsByPropertyId(Long propertyId, Long userId, int page, int size) {
        logger.info("Fetching bookings for property ID: {}, page: {}, size: {}", propertyId, page, size);
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> {
                    logger.error("Property with ID: {} not found", propertyId);
                    return new PropertyNotFoundException("Property not found");
                });
        if (!property.getHost().getId().equals(userId)) {
            logger.warn("Unauthorized action: User with ID: {} is not the host of property with ID: {}", userId, propertyId);
            throw new UnauthorizedActionException("You are not able to view bookings for this property");
        }
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookingPage = bookingRepository.findByPropertyId(propertyId, pageable);
        logger.info("Found {} bookings for property ID: {}", bookingPage.getTotalElements(), propertyId);
        List<BookingResponseDto> bookingResponseDtos = bookingPage.getContent()
                .stream()
                .map(BookingMapper::mapToBookingResponseDto).toList();

        return BookingMapper.mapToBookingResponsePage(bookingPage, bookingResponseDtos);
    }

    @Override
    public List<BookingDatesDto> getBookedDatesByPropertyId(Long propertyId) {
        logger.info("Fetching bookings for property ID: {}", propertyId);
        List<Booking> bookings = bookingRepository.findByPropertyIdAndStatuses(
                propertyId, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        logger.info("Found {} pending/confirmed bookings for property ID: {}", bookings.size(), propertyId);
        return bookings.stream()
                .map(booking -> new BookingDatesDto(booking.getCheckInDate(), booking.getCheckOutDate()))
                .toList();
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
        if (booking.getStatus() != BookingStatus.PENDING) {
            logger.warn("Booking with ID: {} is not in PENDING status", bookingId);
            throw new UnauthorizedActionException("Booking is not in PENDING status");
        }
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking updatedBooking = bookingRepository.save(booking);
        logger.info("Booking with ID: {} successfully confirmed", bookingId);
        return BookingMapper.mapToBookingResponseDto(updatedBooking);
    }

    private boolean isPropertyAvailable(Long propertyId, LocalDate startDate, LocalDate endDate) {
        logger.info("Checking availability for property ID: {} from {} to {}", propertyId, startDate, endDate);
        LocalDate today = LocalDate.now();
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new PropertyNotFoundException("Property not found"));
        if (!property.getAvailable()) {
            logger.warn("Property ID: {} is not available", propertyId);
            return false;
        }

        if (startDate.isBefore(today)) {
            logger.warn("Start date {} is before today {}", startDate, today);
            throw new InvalidBookingDatesException("Start date must be today or in the future");
        }

        List<Booking> bookings = bookingRepository.findByPropertyIdAndStatuses
                (propertyId, List.of(BookingStatus.PENDING, BookingStatus.CONFIRMED));
        for (Booking booking : bookings) {
            if (!(startDate.isAfter(booking.getCheckOutDate()) || startDate.isEqual(booking.getCheckOutDate())) &&
                    !(endDate.isBefore(booking.getCheckInDate()) || endDate.isEqual(booking.getCheckInDate()))) {
                return false;
            }
        }

        logger.info("Property ID: {} is available from {} to {}", propertyId, startDate, endDate);
        return true;
    }
}
