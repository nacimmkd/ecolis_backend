package com.deliveryplatform.users;

public class PasswordNotValidException extends RuntimeException {
    public PasswordNotValidException() {
        super("password is not valid");
    }
}
