package com.deliveryplatform.reviews;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingRepository;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.InvalidDomainStateException;
import com.deliveryplatform.common.exceptions.ResourceNotFoundException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.reviews.dto.CreateReviewRequest;
import com.deliveryplatform.reviews.dto.ReviewResponse;
import com.deliveryplatform.users.User;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewServiceImp implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingRepository bookingRepository;
    private final ReviewMapper reviewMapper;


    public List<ReviewResponse> getReviewsForUser(UUID userId) {
        return reviewRepository.findByRevieweeId(userId)
                .stream()
                .map(reviewMapper::toResponse)
                .toList();
    }

    @Transactional
    public ReviewResponse create(CreateReviewRequest request, UUID reviewerId) {

        var booking = bookingRepository.findBookingWithParticipants(request.bookingId())
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

        assertBookingIsCompleted(booking);
        assertReviewerInvolvedInBooking(booking, reviewerId);
        assertNotAlreadyReviewed(booking.getId(), reviewerId);

        var reviewer = booking.resolveParticipant(reviewerId);
        var reviewee = booking.resolveOtherParticipant(reviewerId);

        var review = buildReview(booking,reviewer,reviewee,request.rating(),request.comment());
        reviewRepository.save(review);

        return reviewMapper.toResponse(review);
    }

    public void remove(UUID reviewId, UUID reviewerId) {
        assertOwnedReviewExists(reviewId, reviewerId);
        reviewRepository.deleteById(reviewId);
    }


    // --------------------------------------------------------

    private void assertNotAlreadyReviewed(UUID bookingId, UUID reviewerId) {
        if (reviewRepository.existsByBookingIdAndReviewerId(bookingId, reviewerId)) {
            throw new ConflictException("You have already reviewed this booking");
        }
    }

    private void assertBookingIsCompleted(Booking booking) {
        if(!booking.isCompleted()) {
            throw new InvalidDomainStateException("Booking must be completed before reviewing");
        }
    }

    private void assertReviewerInvolvedInBooking(Booking booking, UUID reviewerId) {
        if (!booking.involves(reviewerId))
            throw new UnauthorizedActionException("You are not part of this booking");
    }


    private void assertOwnedReviewExists(UUID reviewId, UUID userId) {
        // checks if review exists
        // checks if requester is the one who did the review
        if (!reviewRepository.existsByIdAndReviewerId(reviewId, userId)) {
            throw new UnauthorizedActionException("Action not allowed");
        }
    }


    private Review buildReview(Booking booking, User reviewer, User reviewee, Short rating, String comment) {
        return Review.builder()
                .booking(booking)
                .reviewer(reviewer)
                .reviewee(reviewee)
                .rating(rating)
                .comment(comment)
                .createdAt(OffsetDateTime.now())
                .build();
    }



}
