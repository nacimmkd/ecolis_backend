package com.deliveryplatform.storage.exceptions;

import com.deliveryplatform.common.exceptions.DomainException;

public class StorageException extends DomainException {

    public StorageException(StorageErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}