package com.deliveryplatform.users.exceptions;

public class PasswordNotValidException extends RuntimeException {
    public PasswordNotValidException() {
        super("password is not valid");
    }
}
