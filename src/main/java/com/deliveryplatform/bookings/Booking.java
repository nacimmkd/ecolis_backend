package com.deliveryplatform.bookings;

import com.deliveryplatform.parcels.Parcel;
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
    @JoinColumn(name = "requester_id")
    private User requester;

    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private String status = "pending";

    private BigDecimal price;

    @Column(name = "message", columnDefinition = "TEXT")
    private String message;

    @Column(name = "confirmed_at")
    private OffsetDateTime confirmedAt;

    @Column(name = "picked_up_at")
    private OffsetDateTime pickedUpAt;

    @Column(name = "delivered_at")
    private OffsetDateTime deliveredAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private OffsetDateTime createdAt;
}
