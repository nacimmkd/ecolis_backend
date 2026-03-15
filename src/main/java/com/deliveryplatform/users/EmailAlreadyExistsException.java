package com.deliveryplatform.users;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("Account with email already exists");
    }
}
