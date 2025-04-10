package com.easycontract.service.impl;

import com.easycontract.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;
    
    @Value("${spring.mail.username}")
    private String fromEmail;
    
    @Value("${app.frontend-url:http://localhost:8080}")
    private String frontendUrl;

    @Override
    public void sendPasswordResetEmail(String email, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(email);
        message.setSubject("密码重置");
        message.setText("请点击以下链接重置您的密码：\n\n" +
                frontendUrl + "/reset-password?token=" + resetToken + "\n\n" +
                "该链接24小时内有效，请尽快完成密码重置。");
        
        mailSender.send(message);
    }
}
