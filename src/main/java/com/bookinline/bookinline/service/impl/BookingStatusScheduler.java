package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.BookingStatus;
import com.bookinline.bookinline.repository.BookingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingStatusScheduler {
    private static final Logger logger = LoggerFactory.getLogger(BookingStatusScheduler.class);

    private final BookingRepository bookingRepository;

    public BookingStatusScheduler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateBookingStatusToCheckedOut() {
        logger.info("Starting scheduled task: updateBookingStatusToCheckedOut");

        LocalDate today = LocalDate.now();
        List<Booking> bookingsToCheckOut = bookingRepository.findByStatusAndCheckOutDateBefore(
                BookingStatus.CONFIRMED, today);

        logger.info("Found {} bookings to update to CHECKED_OUT status", bookingsToCheckOut.size());
        for (Booking booking : bookingsToCheckOut) {
            logger.info("Updating booking with ID: {} to CHECKED_OUT status", booking.getId());
            booking.setStatus(BookingStatus.CHECKED_OUT);
        }

        bookingRepository.saveAll(bookingsToCheckOut);

        logger.info("Completed scheduled task: updateBookingStatusToCheckedOut, updated {} bookings",
                bookingsToCheckOut.size());
    }
}