package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.BookingStatus;
import com.bookinline.bookinline.repositories.BookingRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingStatusScheduler {

    private final BookingRepository bookingRepository;

    public BookingStatusScheduler(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void updateBookingStatusToCheckedOut() {
        LocalDate today = LocalDate.now();
        List<Booking> bookingsToCheckOut = bookingRepository.findByStatusAndCheckOutDateBefore(
                BookingStatus.CONFIRMED, today);

        for (Booking booking : bookingsToCheckOut) {
            booking.setStatus(BookingStatus.CHECKED_OUT);
        }

        bookingRepository.saveAll(bookingsToCheckOut);
    }
}