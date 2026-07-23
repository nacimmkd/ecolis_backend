package com.deliveryplatform.profiles.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class ProfileException extends DomainException {

    public ProfileException(ProfileErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}