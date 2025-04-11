package com.easycontract.service;

public interface EmailService {
    void sendPasswordResetEmail(String email, String resetToken);
}
