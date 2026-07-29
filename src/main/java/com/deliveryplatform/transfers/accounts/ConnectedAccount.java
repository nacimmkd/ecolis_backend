package com.deliveryplatform.transfers.accounts;

import com.deliveryplatform.profiles.Profile;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "connected_accounts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class ConnectedAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private Profile profile;

    @Column(name = "stripe_account_id", nullable = false, unique = true)
    private String stripeAccountId;

    @Column(name = "charges_enabled", nullable = false)
    @Builder.Default
    private boolean chargesEnabled = false;

    @Column(name = "payouts_enabled", nullable = false)
    @Builder.Default
    private boolean payoutsEnabled = false;

    @Column(name = "details_submitted", nullable = false)
    @Builder.Default
    private boolean detailsSubmitted = false;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public static ConnectedAccount create(Profile profile, String stripeAccountId) {
        return ConnectedAccount.builder()
                .profile(profile)
                .stripeAccountId(stripeAccountId)
                .build();
    }

    public void syncFromStripe(boolean chargesEnabled, boolean payoutsEnabled, boolean detailsSubmitted) {
        this.chargesEnabled = chargesEnabled;
        this.payoutsEnabled = payoutsEnabled;
        this.detailsSubmitted = detailsSubmitted;
    }

    public boolean isReadyForPayouts() {
        return this.payoutsEnabled;
    }
}