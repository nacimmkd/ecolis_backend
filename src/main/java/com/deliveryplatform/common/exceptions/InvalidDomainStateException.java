package com.deliveryplatform.common.exceptions;

public class InvalidDomainStateException extends RuntimeException {
    public InvalidDomainStateException(String message) {
        super(message);
    }
}
