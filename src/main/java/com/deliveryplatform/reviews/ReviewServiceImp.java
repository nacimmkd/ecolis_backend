package com.deliveryplatform.reviews;

import com.deliveryplatform.bookings.Booking;
import com.deliveryplatform.bookings.BookingService;
import com.deliveryplatform.common.exceptions.ConflictException;
import com.deliveryplatform.common.exceptions.UnauthorizedActionException;
import com.deliveryplatform.users.User;
import com.deliveryplatform.users.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final BookingService bookingService;
    private final UserService userService;
    private final ReviewMapper reviewMapper;


    @Transactional
    public Review create(CreateReviewRequest request, UUID reviewerId) {
        var reviewer = userService.getUserByIdOrThrow(reviewerId);
        var booking = bookingService.getBookingByIdOrThrow(request.bookingId());

        validateReviewer(booking,reviewer);
        assertNotAlreadyReviewed(booking.getId(),reviewer.getId());

        var review = reviewMapper.toEntity(request);
        return reviewRepository.save(review);
    }


    // --------------------------------------------------------

    private void validateReviewer(Booking booking, User reviewer) {
        boolean isSender = booking.getParcel().getUser().getId().equals(reviewer.getId());
        boolean isCarrier = booking.getTrip().getUser().getId().equals(reviewer.getId());
        if(!isSender && !isCarrier) {
            throw new UnauthorizedActionException("You are not part of this booking");
        }
    }

    private void assertNotAlreadyReviewed(UUID bookingId, UUID reviewerId) {
        if (reviewRepository.existsByBookingIdAndReviewerId(bookingId, reviewerId)) {
            throw new ConflictException("You have already reviewed this booking");
        }
    }




}
