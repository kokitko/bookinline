package com.bookinline.bookinline.config;

import com.bookinline.bookinline.repository.BookingRepository;
import com.bookinline.bookinline.repository.PropertyRepository;
import com.bookinline.bookinline.repository.ReviewRepository;
import com.bookinline.bookinline.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "bookinline")
public class BookinlineEndpoint {
    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final BookingRepository bookingRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public BookinlineEndpoint(UserRepository userRepository,
                              PropertyRepository propertyRepository,
                              BookingRepository bookingRepository,
                              ReviewRepository reviewRepository) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.bookingRepository = bookingRepository;
        this.reviewRepository = reviewRepository;
    }

    @ReadOperation
    public Map<String, Object> bookinlineStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", userRepository.count());
        stats.put("totalProperties", propertyRepository.count());
        stats.put("totalBookings", bookingRepository.count());
        stats.put("totalReviews", reviewRepository.count());
        return stats;
    }
}
