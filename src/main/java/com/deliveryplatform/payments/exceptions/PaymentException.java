package com.deliveryplatform.payments.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class PaymentException extends DomainException {

    public PaymentException(PaymentErrorCode errorCode, String message) {
        super(errorCode, message);
    }

    public PaymentException(PaymentErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message, cause);
    }
}