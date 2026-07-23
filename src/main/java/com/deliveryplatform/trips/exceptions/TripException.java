package com.deliveryplatform.trips.exceptions;


import com.deliveryplatform.common.exceptions.DomainException;

public class TripException extends DomainException {

    public TripException(TripErrorCode errorCode, String message) {
        super(errorCode,message);
    }

}