package com.starline.spring.mailer.controller;
/*
@Author hakim a.k.a. Hakim Amarullah
Java Developer
Created on 9/18/2024 1:09 PM
@Last Modified 9/18/2024 1:09 PM
Version 1.0
*/

import com.starline.shared.dto.mail.SendEmailRequest;
import com.starline.shared.dto.mail.SendEmailRequestAmqp;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/email/smtp")
public class EmailController {

    private final RabbitTemplate rabbitTemplate;

    @Value("${send.email.queue}")
    private String sendEmailQueue;

    public EmailController(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendEmail(SendEmailRequest sendEmailRequest) throws IOException {
        SendEmailRequestAmqp emailRequestAmqp = new SendEmailRequestAmqp();
        emailRequestAmqp.setRecipients(sendEmailRequest.getRecipients());
        emailRequestAmqp.setCc(sendEmailRequest.getCc());
        emailRequestAmqp.setAttachments(sendEmailRequest.getEmailAttachments());
        emailRequestAmqp.setSubject(sendEmailRequest.getSubject());
        emailRequestAmqp.setTextBody(sendEmailRequest.getTextBody());
        emailRequestAmqp.setIsHtml(sendEmailRequest.getIsHtml());
        rabbitTemplate.convertAndSend(sendEmailQueue, emailRequestAmqp);
        return ResponseEntity.ok("Send email request sent successfully");
    }


}
