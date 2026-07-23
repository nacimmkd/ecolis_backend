package com.deliveryplatform.addresses.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;
import com.deliveryplatform.common.exceptions.ErrorCode;

public class AddressException extends DomainException {

    public AddressException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
