package com.deliveryplatform.exceptions;

public class InvalidDomainStateException extends RuntimeException {
    public InvalidDomainStateException(String message) {
        super(message);
    }
}
