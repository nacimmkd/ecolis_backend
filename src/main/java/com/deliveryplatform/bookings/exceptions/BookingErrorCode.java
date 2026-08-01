package com.deliveryplatform.bookings.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookingErrorCode implements ErrorCode {

    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND),
    BOOKING_ALREADY_EXISTS(HttpStatus.CONFLICT),
    INVALID_STATE(HttpStatus.CONFLICT),
    NOT_INVOLVED_IN_BOOKING(HttpStatus.FORBIDDEN),
    NOT_CARRIER(HttpStatus.FORBIDDEN),
    INVALID_PICKUP_CODE(HttpStatus.BAD_REQUEST),
    INVALID_DROPOFF_CODE(HttpStatus.BAD_REQUEST),
    MISSING_REQUIRED_DATA(HttpStatus.BAD_REQUEST),
    PAYMENT_REQUIRED(HttpStatus.PAYMENT_REQUIRED);


    private final HttpStatus status;

    BookingErrorCode(HttpStatus status) {
        this.status = status;
    }
    @Override
    public HttpStatus status() {
        return status;
    }
}
