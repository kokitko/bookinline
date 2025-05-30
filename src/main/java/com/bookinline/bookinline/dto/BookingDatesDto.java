package com.bookinline.bookinline.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingDatesDto {
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
}
