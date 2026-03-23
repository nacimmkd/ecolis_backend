package com.deliveryplatform.parcels.exceptions;

public class UnauthorizedParcelActionException extends RuntimeException {
    public UnauthorizedParcelActionException() {
        super("Not allowed to perform this action");
    }
}
