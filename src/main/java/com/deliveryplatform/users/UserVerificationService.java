package com.deliveryplatform.users;

public interface UserVerificationService {
    void save(String email, String code);
    boolean verify(String email, String code);
    void invalidate(String email);
}
