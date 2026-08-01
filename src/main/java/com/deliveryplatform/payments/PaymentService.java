package com.deliveryplatform.payments;

import com.deliveryplatform.payments.dto.PaymentResponse;
import com.deliveryplatform.payments.dto.WebhookRequest;

import java.util.List;
import java.util.UUID;

public interface PaymentService {

    PaymentResponse checkout(UUID bookingId);

    PaymentResponse cancel(UUID bookingId);

    void cancelAll(List<UUID> bookingIds);

    PaymentResponse capture(UUID bookingId);

    PaymentResponse refund(UUID bookingId);

    boolean isAuthorized(UUID bookingId);

    void handleWebHook(WebhookRequest webhookRequest);

}