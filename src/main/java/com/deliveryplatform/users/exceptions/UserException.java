package com.deliveryplatform.users.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class UserException extends DomainException {

    public UserException(UserErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}