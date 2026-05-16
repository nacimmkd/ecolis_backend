package com.deliveryplatform.profiles;


import com.deliveryplatform.images.Image;
import com.deliveryplatform.profiles.dto.ProfileCreateRequest;
import com.deliveryplatform.profiles.dto.ProfileStatsProjection;
import com.deliveryplatform.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String phone;

    @OneToOne
    @JoinColumn(name = "avatar_image_id")
    private Image avatar;

    @Transient
    @Builder.Default
    private Double avgRating = null;

    @Transient
    @Builder.Default
    private Long reviewCount = 0L;

    @Transient
    @Builder.Default
    private Long completedTrips = 0L;

    @Transient
    @Builder.Default
    private Long deliveredParcels = 0L;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @MapsId
    private User user;


    public static Profile createFromRequest(ProfileCreateRequest request) {
        return Profile.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .avatar(null)
                .build();
    }

    public void setStatistics(ProfileStatsProjection stats) {
        this.avgRating = stats.getAvgRating();
        this.reviewCount = stats.getReviewCount();
        this.completedTrips = stats.getCompletedTrips();
        this.deliveredParcels = stats.getDeliveredParcels();
    }


}
