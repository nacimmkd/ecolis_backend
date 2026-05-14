package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.users.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    private final UserMapper userMapper;

    public ReviewDto toDto(Review review) {
        return ReviewDto.builder()
                .id(review.getId())
                .reviewer(userMapper.toSummaryDto(review.getReviewer()))
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt())
                .build();
    }

    public List<ReviewDto> toDto(List<Review> reviews) {
        return reviews.stream().map(this::toDto).toList();
    }
}
