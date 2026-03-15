package com.deliveryplatform.users;


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
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = false;

    @Column(name = "registered_at")
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private boolean isActive = true;

    @OneToOne( mappedBy = "user", cascade = CascadeType.ALL)
    private Profile profile;


    public void setProfile(Profile profile) {
        this.profile = profile;
        profile.setUser(this);
    }

}
