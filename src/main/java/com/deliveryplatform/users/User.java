package com.deliveryplatform.users;

import com.deliveryplatform.cutomers.CustomerProfile;
import com.deliveryplatform.drivers.DriverProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String phone;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "is_active")
    private boolean isActive = true;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private DriverProfile driverProfile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private CustomerProfile customerProfile;
}
