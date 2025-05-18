package com.bookinline.bookinline.specification;

import com.bookinline.bookinline.dto.PropertyFilterDto;
import com.bookinline.bookinline.entity.Booking;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.enums.BookingStatus;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PropertySpecification implements Specification<Property> {

    private final PropertyFilterDto filter;

    public PropertySpecification(PropertyFilterDto filter) {
        this.filter = filter;
    }

    @Override
    public Predicate toPredicate(Root<Property> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();

        if (filter.getTitle() != null) {
            predicates.add(cb.like(cb.lower(root.get("title")), "%" + filter.getTitle().toLowerCase() + "%"));
        }
        if (filter.getCity() != null) {
            predicates.add(cb.equal(cb.lower(root.get("city")), filter.getCity().toLowerCase()));
        }
        if (filter.getAddress() != null) {
            predicates.add(cb.like(cb.lower(root.get("address")), "%" + filter.getAddress().toLowerCase() + "%"));
        }

        if (filter.getPropertyType() != null) {
            predicates.add(cb.equal(root.get("propertyType"), filter.getPropertyType()));
        }

        if (filter.getMinFloorArea() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("floorArea"), filter.getMinFloorArea()));
        }
        if (filter.getMaxFloorArea() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("floorArea"), filter.getMaxFloorArea()));
        }
        if (filter.getMinBedrooms() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("bedrooms"), filter.getMinBedrooms()));
        }
        if (filter.getMaxBedrooms() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("bedrooms"), filter.getMaxBedrooms()));
        }
        if (filter.getMinPrice() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerNight"), filter.getMinPrice()));
        }
        if (filter.getMaxPrice() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("pricePerNight"), filter.getMaxPrice()));
        }
        if (filter.getMinGuests() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("maxGuests"), filter.getMinGuests()));
        }
        if (filter.getMaxGuests() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("maxGuests"), filter.getMaxGuests()));
        }
        if (filter.getMinRating() != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("averageRating"), filter.getMinRating()));
        }
        if (filter.getMaxRating() != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("averageRating"), filter.getMaxRating()));
        }

        if (filter.getSortBy() != null) {
            Path<?> sortField = root.get(filter.getSortBy());
            if ("DESC".equalsIgnoreCase(filter.getSortOrder())) {
                query.orderBy(cb.desc(sortField));
            } else {
                query.orderBy(cb.asc(sortField));
            }
        }

        return cb.and(predicates.toArray(new Predicate[0]));
    }

    public static Specification<Property> withAvailability() {
        return (root, query, criteriaBuilder) ->
            criteriaBuilder.isTrue(root.get("available"));
    }

    public static Specification<Property> availableBetween(LocalDate checkIn, LocalDate checkOut) {
        return (root, query, cb) -> {
            Subquery<Long> subquery = query.subquery(Long.class);
            Root<Booking> bookingRoot = subquery.from(Booking.class);

            subquery.select(bookingRoot.get("property").get("id"));
            subquery.where(
                    cb.equal(bookingRoot.get("property"), root),
                    bookingRoot.get("status").in(List.of(BookingStatus.CONFIRMED, BookingStatus.PENDING)),
                    cb.not(
                            cb.or(
                                    cb.lessThanOrEqualTo(bookingRoot.get("checkOutDate"), checkIn),
                                    cb.greaterThanOrEqualTo(bookingRoot.get("checkInDate"), checkOut)
                            )
                    )
            );
            return cb.not(cb.exists(subquery));
        };
    }
}