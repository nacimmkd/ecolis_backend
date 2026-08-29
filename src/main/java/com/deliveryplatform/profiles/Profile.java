package com.deliveryplatform.profiles;

import com.deliveryplatform.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@Getter
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private UUID id;

    @Column(name = "first_name")
    @Setter(AccessLevel.PACKAGE)
    private String firstName;

    @Column(name = "last_name")
    @Setter(AccessLevel.PACKAGE)
    private String lastName;

    @Setter(AccessLevel.PACKAGE)
    private String phone;

    @Setter(AccessLevel.PACKAGE)
    private String country;

    @Column(name = "avatar_key")
    @Setter(AccessLevel.PACKAGE)
    private String avatarKey;

    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating;

    @Column(name = "review_count")
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "completed_trips")
    @Builder.Default
    private int completedTrips = 0;

    @Column(name = "sent_parcels")
    @Builder.Default
    private int sentParcels = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @MapsId
    @Setter
    private User user;

    public static Profile create(String firstName, String lastName) {
        return Profile.builder()
                .firstName(firstName)
                .lastName(lastName)
                .build();
    }

    // ---- stats ------------------------------------------------------------

    void updateReviewStats(int reviewCount, BigDecimal avgRating) {
        this.reviewCount = reviewCount;
        this.avgRating = avgRating;
    }

    void updateCompletedTrips(int completedTrips) {
        this.completedTrips = completedTrips;
    }

    void updateSentParcels(int sentParcels) {
        this.sentParcels = sentParcels;
    }
}