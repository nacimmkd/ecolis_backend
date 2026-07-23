package com.deliveryplatform.trips.exceptions;

import com.deliveryplatform.common.exceptions.ErrorCode;
import org.springframework.http.HttpStatus;

public enum TripErrorCode implements ErrorCode {

    TRIP_NOT_FOUND(HttpStatus.NOT_FOUND),
    TRIP_NOT_OWNED(HttpStatus.FORBIDDEN),
    TRIP_NOT_PUBLISHED(HttpStatus.CONFLICT),
    TRIP_DELETED(HttpStatus.CONFLICT),
    TRIP_FULL(HttpStatus.CONFLICT),
    MAX_DETOUR_EXCEEDED(HttpStatus.CONFLICT),
    INVALID_STATE_TRANSITION(HttpStatus.CONFLICT),

    STOP_NOT_FOUND(HttpStatus.NOT_FOUND),
    STOP_ALREADY_DELETED(HttpStatus.CONFLICT),
    STOP_DUPLICATE_ADDRESS(HttpStatus.CONFLICT),
    STOP_SEQUENCE_INVALID(HttpStatus.BAD_REQUEST),

    WEIGHT_BELOW_MINIMUM(HttpStatus.CONFLICT),
    WEIGHT_BELOW_RESERVED(HttpStatus.CONFLICT),
    WEIGHT_INSUFFICIENT_REMAINING(HttpStatus.CONFLICT);


    private final HttpStatus status;

    TripErrorCode(HttpStatus status) {
        this.status = status;
    }

    @Override
    public HttpStatus status() {
        return status;
    }
}