package com.bookinline.bookinline.unit.mapper;

import com.bookinline.bookinline.dto.ReviewRequestDto;
import com.bookinline.bookinline.dto.ReviewResponseDto;
import com.bookinline.bookinline.dto.ReviewResponsePage;
import com.bookinline.bookinline.entity.Property;
import com.bookinline.bookinline.entity.Review;
import com.bookinline.bookinline.entity.User;
import com.bookinline.bookinline.mapper.ReviewMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ReviewMapperTest {
    private final ReviewMapper reviewMapper = new ReviewMapper();

    @Test
    void shouldMapToReviewResponseDto() {
        User user = new User();
        user.setFullName("John Doe");

        Property property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");

        Review review = new Review(1L, 5, "Great place!", LocalDateTime.now(), user, property);

        ReviewResponseDto responseDto = reviewMapper.mapToReviewResponseDto(review);

        assertThat(responseDto).isNotNull();
        assertThat(responseDto.getId()).isEqualTo(review.getId());
        assertThat(responseDto.getRating()).isEqualTo(review.getRating());
        assertThat(responseDto.getComment()).isEqualTo(review.getComment());
        assertThat(responseDto.getCreatedAt()).isEqualTo(review.getCreatedAt());
        assertThat(responseDto.getAuthorName()).isEqualTo(user.getFullName());
    }

    @Test
    void shouldMapToReviewEntity() {
        ReviewRequestDto requestDto = new ReviewRequestDto(5, "Great place!");

        Review review = reviewMapper.mapToReviewEntity(requestDto);

        assertThat(review).isNotNull();
        assertThat(review.getRating()).isEqualTo(requestDto.getRating());
        assertThat(review.getComment()).isEqualTo(requestDto.getComment());
        assertThat(review.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldMapToReviewResponseEntity() {
        User user = new User();
        user.setFullName("John Doe");

        Property property = new Property();
        property.setId(1L);
        property.setTitle("Test Property");

        List<Review> reviews = List.of(
                new Review(1L, 5, "Great place!", LocalDateTime.now(), user, property),
                new Review(2L, 4, "Good experience", LocalDateTime.now(), user, property)
        );

        Page<Review> reviewPage = mock(Page.class);
        when(reviewPage.getNumber()).thenReturn(0);
        when(reviewPage.getSize()).thenReturn(2);
        when(reviewPage.getTotalElements()).thenReturn(2L);
        when(reviewPage.getTotalPages()).thenReturn(1);
        when(reviewPage.isLast()).thenReturn(true);
        when(reviewPage.getContent()).thenReturn(reviews);

        ReviewResponsePage reviewResponsePage = reviewMapper.mapToReviewResponsePage(reviewPage);

        assertThat(reviewResponsePage).isNotNull();
        assertThat(reviewResponsePage.getPage()).isEqualTo(0);
        assertThat(reviewResponsePage.getSize()).isEqualTo(2);
        assertThat(reviewResponsePage.getTotalElements()).isEqualTo(2L);
        assertThat(reviewResponsePage.getTotalPages()).isEqualTo(1);
        assertThat(reviewResponsePage.isLast()).isTrue();
        assertThat(reviewResponsePage.getReviews()).hasSize(2);
        assertThat(reviewResponsePage.getReviews().get(0).getId()).isEqualTo(reviews.get(0).getId());
        assertThat(reviewResponsePage.getReviews().get(1).getId()).isEqualTo(reviews.get(1).getId());
    }
}
