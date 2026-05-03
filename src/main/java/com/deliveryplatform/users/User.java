package com.deliveryplatform.users;


import com.deliveryplatform.profiles.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "users")
@SQLRestriction("deleted = false")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "is_verified")
    @Builder.Default
    private boolean isVerified = true; // just for dev, to be changed later

    @Column(name = "registered_at")
    @Builder.Default
    private OffsetDateTime registeredAt = OffsetDateTime.now();

    @Column(name = "deleted")
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private OffsetDateTime deletedAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private Profile profile;

    public void setProfile(Profile profile) {
        if (profile != null) {
            this.profile = profile;
            profile.setUser(this);
        }
    }

    public void softDelete(){
        this.deleted = true;
        this.setEmail("_deleted_" + UUID.randomUUID() + "_" + this.getEmail());
        this.setDeletedAt(OffsetDateTime.now());
    }

}
