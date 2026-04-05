package com.deliveryplatform.common.exceptions;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(Class<?> serviceClass, String message) {
        super("[%s] %s".formatted(serviceClass.getSimpleName(), message));
    }

    public ExternalServiceException(Class<?> serviceClass, String message, Throwable cause) {
        super("[%s] %s".formatted(serviceClass.getSimpleName(), message), cause);
    }
}