package com.deliveryplatform.drivers;

import com.deliveryplatform.users.User;
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
@Table(name = "driver_profiles")
public class DriverProfile {

    @Id
    @GeneratedValue
    private UUID id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "avg_rating", precision = 2, scale = 1)
    private BigDecimal avgRating = BigDecimal.valueOf(0.0);

    @Column(name = "total_deliveries")
    private int totalDeliveries = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "vehicle_type")
    private VehicleType vehicleType;

    @Column(name = "max_weight_kg", precision = 8, scale = 2)
    private BigDecimal maxWeightKg;

    @Column(name = "max_volume_m3", precision = 8, scale = 3)
    private BigDecimal maxVolumeM3;

    private String iban;

    @Column(name = "is_available")
    private boolean isAvailable = true;

}