package dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyResponseDto {
    private Long id;
    private String title;
    private String description;
    private String address;
    private BigDecimal pricePerNight;
    private Integer maxGuests;
    private Boolean available;
    private Double averageRating;
}
