package dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    private int rating;
    private String comment;
}
