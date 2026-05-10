package com.deliveryplatform.reviews;

import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.users.UserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    // public
    @GetMapping("/{id}")
    public List<ReviewDto> getUserReviews(@PathVariable UUID id) {
        return reviewService.getUserReviews(id);
    }

    @GetMapping
    public List<ReviewDto> getReviewsMe(
            @AuthenticationPrincipal UserPrincipal user){
        return reviewService.getUserReviews(user.getId());
    }

    @PostMapping
    public ResponseEntity<ReviewDto> create(
            @Valid @RequestBody CreateReviewRequest request,
            @AuthenticationPrincipal UserPrincipal user,
            UriComponentsBuilder uriBuilder
            ) {

        var reviewDto = reviewService.create(request,user.getId());
        var uri = uriBuilder.path("/api/v1/reviews/{id}").build(reviewDto.id());
        return ResponseEntity.created(uri).body(reviewDto);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable UUID id) {

        reviewService.remove(id, user.getId());
        return ResponseEntity.noContent().build();
    }

}
