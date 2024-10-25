package com.blps.lab4.services;

import com.blps.lab4.model.emailmessage.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    public void sendEmail(EmailMessage emailMessage) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("vseslav.demidovich@yandex.ru");
        message.setTo(emailMessage.getRecipientEmail());
        message.setSubject(emailMessage.getTopic());
        message.setText(emailMessage.getMessage());

        mailSender.send(message);
    }
}
