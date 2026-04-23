package com.deliveryplatform.users;

public interface UserVerificationService {
    void send(String email, String code);

    boolean verify(String email, String code);

    boolean exists(String email);
}
