package com.deliveryplatform.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByRequestId(UUID requestId);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByRequestIdAndStatus(UUID requestId, PaymentStatus status);
}