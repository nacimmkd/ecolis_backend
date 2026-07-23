package com.deliveryplatform.reviews;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.bookings.exceptions.BookingErrorCode;
import com.deliveryplatform.bookings.exceptions.BookingException;
import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewDto;
import com.deliveryplatform.reviews.exceptions.ReviewErrorCode;
import com.deliveryplatform.reviews.exceptions.ReviewException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;

    public List<ReviewDto> getUserReviews(UUID userId) {
        return reviewMapper.toDto(reviewRepository.findByRevieweeId(userId));
    }

    @Transactional
    public ReviewDto create(CreateReviewRequest request, UUID reviewerId) {
        var booking = getBookingByIdOrThrow(request.bookingId());
        booking.assertIsCompleted();
        booking.assertUserInvolved(reviewerId);
        assertNotAlreadyReviewed(booking.getId(), reviewerId);
        var reviewer = booking.resolveParticipant(reviewerId);
        var reviewee = booking.resolveOtherParticipant(reviewerId);
        var review = Review.create(booking, reviewer, reviewee, request.rating(), request.comment());
        return reviewMapper.toDto(reviewRepository.save(review));
    }

    public void remove(UUID reviewId, UUID reviewerId) {
        assertOwnedReviewExists(reviewId, reviewerId);
        reviewRepository.deleteById(reviewId);
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
}