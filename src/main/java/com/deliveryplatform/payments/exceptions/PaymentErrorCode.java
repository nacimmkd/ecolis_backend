package com.deliveryplatform.payments.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum PaymentErrorCode implements ErrorCode {

    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND),
    REQUEST_NOT_PAYABLE(HttpStatus.CONFLICT),
    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT),
    PAYMENT_INVALID_STATE(HttpStatus.CONFLICT),
    NOT_PAYER(HttpStatus.FORBIDDEN),
    INVALID_WEBHOOK_SIGNATURE(HttpStatus.BAD_REQUEST),
    STRIPE_REQUEST_FAILED(HttpStatus.BAD_GATEWAY);

    private final HttpStatus status;

    PaymentErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}