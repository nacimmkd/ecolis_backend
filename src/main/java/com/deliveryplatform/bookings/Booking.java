package com.deliveryplatform.bookings;

import com.deliveryplatform.parcels.Parcel;
import com.deliveryplatform.reviews.Review;
import com.deliveryplatform.trips.Trip;
import com.deliveryplatform.users.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_booking_trip_parcel", columnNames = {"trip_id", "parcel_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "trip_id")
    private Trip trip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parcel_id")
    private Parcel parcel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrier_id")
    private User carrier;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private BookingStatus status = BookingStatus.PENDING;

    private BigDecimal price;

    @Column(name = "accepted_at")
    private OffsetDateTime acceptedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "created_at", updatable = false)
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();


    public boolean isCompleted(){
        return BookingStatus.COMPLETED.equals(status);
    }

    public boolean involves(UUID userId) {
        return this.sender.getId().equals(userId) || this.carrier.getId().equals(userId);
    }

    public User resolveParticipant(UUID userId) {
        return this.sender.getId().equals(userId) ? this.sender : this.carrier;
    }

    public User resolveOtherParticipant(UUID userId) {
        return this.sender.getId().equals(userId) ? this.carrier : this.sender;
    }
}
