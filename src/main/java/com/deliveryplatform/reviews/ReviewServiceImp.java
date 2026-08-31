package com.deliveryplatform.reviews;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.reviews.events.ReviewCreatedEvent;
import com.deliveryplatform.reviews.events.ReviewDeletedEvent;
import com.deliveryplatform.reviews.exceptions.ReviewErrorCode;
import com.deliveryplatform.reviews.exceptions.ReviewException;
import com.deliveryplatform.users.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;
    private final ApplicationEventPublisher eventPublisher;

    public Page<ReviewDto> getUserReviews(UUID userId, Pageable pageable) {
        return reviewRepository.findByRevieweeId(userId, pageable)
                .map(reviewMapper::toDto);
    }

    @Transactional
    public ReviewDto create(CreateReviewRequest request, UUID reviewerId) {
        var booking = getBookingByIdOrThrow(request.bookingId());
        booking.assertIsCompleted();
        booking.assertUserInvolved(reviewerId);
        assertNotAlreadyReviewed(booking.getId(), reviewerId);
        var reviewer = booking.resolveParticipant(reviewerId);
        var reviewee = booking.resolveOtherParticipant(reviewerId);
        assertIsNotSelfReview(reviewee, reviewer);
        var review = Review.create(booking, reviewer, reviewee, request.rating(), request.comment());
        var savedReview = reviewRepository.save(review);

        eventPublisher.publishEvent(new ReviewCreatedEvent(reviewee, savedReview.getId()));

        return reviewMapper.toDto(savedReview);
    }

    @Transactional
    public void remove(UUID reviewId, UUID reviewerId) {
        assertOwnedReviewExists(reviewId, reviewerId);
        var review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND, "Review not found"));

        reviewRepository.deleteById(reviewId);
        eventPublisher.publishEvent(new ReviewDeletedEvent(review.getReviewee().getId()));
    }

    // --------------------------------------------------------

    private void assertNotAlreadyReviewed(UUID bookingId, UUID reviewerId) {
        if (reviewRepository.existsByBookingIdAndReviewerId(bookingId, reviewerId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_ALREADY_EXISTS, "Booking has already been reviewed");
        }
    }

    private Booking getBookingByIdOrThrow(UUID bookingId) {
        return bookingRepository.findBookingById(bookingId)
                .orElseThrow(() -> new BookingException(BookingErrorCode.BOOKING_NOT_FOUND, "Booking not found"));
    }

    private void assertOwnedReviewExists(UUID reviewId, UUID userId) {
        if (!reviewRepository.existsByIdAndReviewerId(reviewId, userId)) {
            throw new ReviewException(ReviewErrorCode.REVIEW_NOT_FOUND, "Review not found");
        }
    }

    private void assertIsNotSelfReview(User reviewee, User reviewer) {
        if (reviewee == null || reviewer == null) {
            throw new  IllegalArgumentException("reviewee or reviewer is null");
        }
        if (reviewer.getId().equals(reviewee.getId())) {
            throw new ReviewException(ReviewErrorCode.SELF_REVIEW_NOT_ALLOWED, "You can not review your self");
        }
    }
}