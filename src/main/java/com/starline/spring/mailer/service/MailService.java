package com.starline.spring.mailer.service;
/*
@Author hakim a.k.a. Hakim Amarullah
Java Developer
Created on 9/18/2024 11:31 AM
@Last Modified 9/18/2024 11:31 AM
Version 1.0
*/

import com.starline.shared.dto.mail.EmailAttachment;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }


    public void sendMessage(
            List<String> toRecipients,
            List<String> ccRecipients,
            String subject,
            String text,
            boolean isHtml,
            List<EmailAttachment> attachments) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();


        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        // Set the email properties
        helper.setFrom(sender);
        helper.setTo(toRecipients.toArray(new String[0]));
        if (ccRecipients != null && !ccRecipients.isEmpty()) {
            helper.setCc(ccRecipients.toArray(new String[0]));
        }
        helper.setSubject(subject);
        helper.setText(text, isHtml);


        // Add attachments
        if (attachments != null) {
            for (var entry : attachments) {
                String filename = Optional.ofNullable(entry.getFileName()).orElse(RandomStringUtils.randomAlphanumeric(20));
                byte[] content = entry.getContent();
                ByteArrayResource byteArrayResource = new ByteArrayResource(content);
                helper.addAttachment(filename, byteArrayResource);
            }
        }

        // Send the email
        javaMailSender.send(message);

    }

}
