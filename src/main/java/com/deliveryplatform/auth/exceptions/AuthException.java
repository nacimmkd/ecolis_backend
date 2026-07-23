package com.deliveryplatform.auth.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;
import com.deliveryplatform.common.exceptions.ErrorCode;

public class AuthException extends DomainException {

    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
