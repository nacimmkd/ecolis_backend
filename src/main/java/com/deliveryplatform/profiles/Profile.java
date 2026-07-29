package com.deliveryplatform.profiles;

import com.deliveryplatform.images.Image;
import com.deliveryplatform.profiles.dto.ProfileCreateRequest;
import com.deliveryplatform.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @OneToOne
    @JoinColumn(name = "avatar_image_id")
    @Setter(AccessLevel.PACKAGE)
    private Image avatar;

    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating;

    @Column(name = "review_count")
    @Builder.Default
    private int reviewCount = 0;

    @Column(name = "completed_trips")
    @Builder.Default
    private int completedTrips = 0;

    @Column(name = "delivered_parcels")
    @Builder.Default
    private int deliveredParcels = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @MapsId
    @Setter
    private User user;

    public static Profile createFromRequest(ProfileCreateRequest request) {
        return Profile.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .country(request.country())
                .avatar(null)
                .build();
    }
}