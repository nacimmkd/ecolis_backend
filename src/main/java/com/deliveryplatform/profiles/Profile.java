package com.deliveryplatform.profiles;


import com.deliveryplatform.images.Image;
import com.deliveryplatform.users.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating = null;

    @Column(name = "total_deliveries")
    private int totalDeliveries = 0;

    @Column(name = "total_orders")
    private int totalOrders = 0;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id")
    @JsonIgnore
    @MapsId
    private User user;


}
