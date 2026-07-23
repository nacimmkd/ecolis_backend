package com.deliveryplatform.reviews.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class ReviewException extends DomainException {

    public ReviewException(ReviewErrorCode errorCode, String message) {
        super(errorCode,message);
    }
}