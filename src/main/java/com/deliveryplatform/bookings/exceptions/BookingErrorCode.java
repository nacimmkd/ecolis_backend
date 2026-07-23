package com.deliveryplatform.bookings.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum BookingErrorCode implements ErrorCode {

    BOOKING_NOT_FOUND(HttpStatus.NOT_FOUND),
    INVALID_STATE(HttpStatus.CONFLICT),
    NOT_INVOLVED_IN_BOOKING(HttpStatus.FORBIDDEN),
    INVALID_PICKUP_CODE(HttpStatus.BAD_REQUEST),
    INVALID_DROPOFF_CODE(HttpStatus.BAD_REQUEST),
    REQUEST_NOT_ACCEPTED(HttpStatus.CONFLICT);


    private final HttpStatus status;

    BookingErrorCode(HttpStatus status) {
        this.status = status;
    }
    @Override
    public HttpStatus status() {
        return status;
    }
}
