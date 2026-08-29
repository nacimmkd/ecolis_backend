package com.deliveryplatform.profiles;

import com.deliveryplatform.bookings.events.BookingCompletedEvent;
import com.deliveryplatform.parcels.ParcelRepository;
import com.deliveryplatform.parcels.ParcelState;
import com.deliveryplatform.profiles.exceptions.ProfileErrorCode;
import com.deliveryplatform.profiles.exceptions.ProfileException;
import com.deliveryplatform.reviews.ReviewRepository;
import com.deliveryplatform.reviews.events.ReviewCreatedEvent;
import com.deliveryplatform.reviews.events.ReviewDeletedEvent;
import com.deliveryplatform.trips.TripRepository;
import com.deliveryplatform.trips.TripState;
import com.deliveryplatform.trips.events.TripCompletedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProfileStatsListener {

    private final ProfileRepository profileRepository;
    private final ParcelRepository parcelRepository;
    private final TripRepository tripRepository;
    private final ReviewRepository reviewRepository;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    public void onBookingCompleted(BookingCompletedEvent event) {
        var senderId = event.parcelSender().getId();
        var sentParcels = parcelRepository.countByOwner_IdAndState(senderId, ParcelState.DELIVERED);
        getProfileOrThrow(senderId).updateSentParcels((int) sentParcels);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    public void onTripCompleted(TripCompletedEvent event) {
        var completedTrips = tripRepository.countByOwner_IdAndState(event.ownerId(), TripState.COMPLETED);
        getProfileOrThrow(event.ownerId()).updateCompletedTrips((int) completedTrips);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    public void onReviewCreated(ReviewCreatedEvent event) {
        recomputeReviewStats(event.revieweeId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    @Transactional
    public void onReviewDeleted(ReviewDeletedEvent event) {
        recomputeReviewStats(event.revieweeId());
    }

    // PRIVATE ─────────────────────────────────────────────────────────────────

    private void recomputeReviewStats(UUID revieweeId) {
        var reviewCount = reviewRepository.countByReviewee_Id(revieweeId);
        var averageRating = reviewRepository.findAverageRatingByRevieweeId(revieweeId);
        var roundedAverage = averageRating == null
                ? null
                : BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP);

        getProfileOrThrow(revieweeId).updateReviewStats((int) reviewCount, roundedAverage);
    }

    private Profile getProfileOrThrow(UUID id) {
        return profileRepository.findProfileById(id)
                .orElseThrow(() -> new ProfileException(ProfileErrorCode.PROFILE_NOT_FOUND, "Profile not found"));
    }
}