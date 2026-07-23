package com.deliveryplatform.messages.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class MessageException extends DomainException {

    public MessageException(MessageErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}