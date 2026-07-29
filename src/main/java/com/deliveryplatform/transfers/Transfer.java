package com.deliveryplatform.transfers;

import com.deliveryplatform.payments.Payment;
import com.deliveryplatform.transfers.accounts.ConnectedAccount;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "transfers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder(access = AccessLevel.PRIVATE)
public class Transfer {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = true)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "connected_account_id", nullable = false)
    private ConnectedAccount connectedAccount;

    @Column(name = "stripe_transfer_id", nullable = false, unique = true)
    private String stripeTransferId;

    @Column(nullable = false)
    private long amount;

    @Column(nullable = false)
    private String currency;

    @Column(name = "created_at")
    @Builder.Default
    private OffsetDateTime createdAt = OffsetDateTime.now();

    public static Transfer create(Payment payment, ConnectedAccount connectedAccount, String stripeTransferId,
                                   long amount, String currency) {
        return Transfer.builder()
                .payment(payment)
                .connectedAccount(connectedAccount)
                .stripeTransferId(stripeTransferId)
                .amount(amount)
                .currency(currency)
                .build();
    }
}