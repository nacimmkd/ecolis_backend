package com.deliveryplatform.users;

import com.deliveryplatform.profiles.Profile;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@SQLRestriction("deleted = false")
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private boolean verified = false;

    @Column(name = "registered_at")
    @Builder.Default
    private OffsetDateTime registeredAt = OffsetDateTime.now();

    @Column(name = "deleted")
    @Builder.Default
    private boolean deleted = false;

    @Column(name = "deleted_at")
    @Builder.Default
    private OffsetDateTime deletedAt = null;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    private Profile profile;

    public static User create(String email, String hashedPassword, Profile profile) {
        var user = User.builder()
                .email(email)
                .password(hashedPassword)
                .role(Role.USER)
                .verified(false)
                .build();
        user.setProfile(profile);
        return user;
    }

    private void setProfile(Profile profile) {
        if (profile != null) {
            profile.setUser(this);
            this.profile = profile;
        }
    }

    public void delete() {
        this.deleted = true;
        this.email = "_deleted_" + UUID.randomUUID() + "_" + this.getEmail();
        this.deletedAt = OffsetDateTime.now();
    }

    public void verify() {
        this.verified = true;
    }

    public void updatePassword(String newHashedPassword) {
        this.password = newHashedPassword;
    }
}