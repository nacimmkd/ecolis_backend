package com.deliveryplatform.payments;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    Optional<Payment> findByBookingId(UUID bookingId);

    List<Payment> findByBookingIdIn(Collection<UUID> bookingIds);

    Optional<Payment> findByStripePaymentIntentId(String stripePaymentIntentId);

    boolean existsByBookingIdAndStatus(UUID bookingId, PaymentStatus status);
}