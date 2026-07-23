package com.deliveryplatform.parcels.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class ParcelException extends DomainException {

    public ParcelException(ParcelErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}