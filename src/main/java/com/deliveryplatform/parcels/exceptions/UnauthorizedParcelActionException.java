package com.deliveryplatform.parcels.exceptions;

public class UnauthorizedActionException extends RuntimeException {
    public UnauthorizedActionException() {
        super("Not allowed to perform this action");
    }
}
