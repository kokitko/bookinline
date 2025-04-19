package com.bookinline.bookinline.service.impl;

import com.bookinline.bookinline.dto.BookingRequestDto;
import com.bookinline.bookinline.dto.BookingResponseDto;
import com.bookinline.bookinline.dto.BookingResponsePage;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.BookingStatus;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import com.bookinline.bookinline.repositories.BookingRepository;
import com.bookinline.bookinline.repositories.PropertyRepository;
import com.bookinline.bookinline.repositories.UserRepository;
import com.bookinline.bookinline.service.BookingService;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
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
    public BookingResponseDto bookProperty(BookingRequestDto bookingRequestDto) {
        Booking booking = mapToBookingEntity(bookingRequestDto);
        Booking savedBooking = bookingRepository.save(booking);
        return mapToBookingResponseDto(savedBooking);
    }

    @Override
    public BookingResponseDto cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));
        booking.setStatus(BookingStatus.CANCELLED);
        Booking updatedBooking = bookingRepository.save(booking);
        return mapToBookingResponseDto(updatedBooking);
    }

    @Override
    public BookingResponsePage getBookingsByUserId(Long userId, int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Booking> bookingPage = bookingRepository.findByGuestId(userId, pageable);
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
    public boolean isPropertyAvailable(Long propertyId, String startDate, String endDate) {
        List<Booking> bookings = bookingRepository.findByPropertyId(propertyId, Pageable.unpaged()).getContent();
        LocalDate startLocalDate = LocalDate.parse(startDate);
        LocalDate endLocalDate = LocalDate.parse(endDate);
        for (Booking booking : bookings) {
            if (booking.getCheckInDate().isBefore(startLocalDate) && booking.getCheckOutDate().isAfter(endLocalDate)) {
                return false;
            }
        }
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

    private Booking mapToBookingEntity(BookingRequestDto bookingRequestDto) {
        Property property = propertyRepository.findById(bookingRequestDto.getPropertyId())
                .orElseThrow(() -> new RuntimeException("Property not found"));
        User guest = userRepository.findById(bookingRequestDto.getGuestId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        return Booking.builder()
                .checkInDate(bookingRequestDto.getCheckInDate())
                .checkOutDate(bookingRequestDto.getCheckOutDate())
                .property(property)
                .guest(guest)
                .status(BookingStatus.PENDING)
                .build();
    }
}
