package com.deliveryplatform.notifications.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class NotificationException extends DomainException {

    public NotificationException(NotificationErrorCode errorCode, String message) {
        super(errorCode, message);
    }

}