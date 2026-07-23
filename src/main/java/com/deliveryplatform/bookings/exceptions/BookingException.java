package com.deliveryplatform.bookings.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;
import com.deliveryplatform.common.exceptions.ErrorCode;

public class BookingException extends DomainException {

    public BookingException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
