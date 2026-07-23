package com.deliveryplatform.requests.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class RequestException extends DomainException {

    public RequestException(RequestErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}