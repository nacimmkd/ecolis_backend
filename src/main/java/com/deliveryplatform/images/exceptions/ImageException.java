package com.deliveryplatform.images.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;
import com.deliveryplatform.common.exceptions.ErrorCode;

public class ImageException extends DomainException {

    public ImageException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
