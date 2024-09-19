package com.starline.spring.mailer.consumer;
/*
@Author hakim a.k.a. Hakim Amarullah
Java Developer
Created on 9/18/2024 3:18 PM
@Last Modified 9/18/2024 3:18 PM
Version 1.0
*/

import com.rabbitmq.client.Channel;
import com.starline.shared.dto.mail.SendEmailRequestAmqp;
import com.starline.spring.mailer.config.RabbitMqConfig;
import com.starline.spring.mailer.service.MailService;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SendMailConsumer {

    private final MailService mailService;

    public SendMailConsumer(MailService mailService) {
        this.mailService = mailService;
    }

    @Retryable()
    @RabbitListener(queues = "${send.email.queue}", containerFactory = RabbitMqConfig.RABBIT_LISTENER_CONTAINER_FACTORY)
    public void receiveMessage(@Payload SendEmailRequestAmqp emailRequest, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws MessagingException {
        log.info("Received message: {} Tag: {} Channel Num: {}", emailRequest, tag, channel.getChannelNumber());

        mailService.sendMessage(emailRequest.getRecipients(),
                emailRequest.getCc(),
                emailRequest.getSubject(),
                emailRequest.getTextBody(),
                emailRequest.getIsHtml(),
                emailRequest.getAttachments());
        log.info("Email sent successfully!");
    }

    @Recover
    public void recover(Exception e) {
        log.error("{}", e.getMessage(), e);
    }

}
