package com.metrics.service;

import com.metrics.dto.MailDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Autowired
    private JavaMailSender mailSender;

    /**
     * all exception handled by default spring async thread uncaught exception handler
     * @see org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
     */
    @Async
    public void sendEmail(MailDto mailDto) {
        final SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(mailDto.getTo());
        msg.setFrom(mailDto.getFrom());
        msg.setSubject(mailDto.getSubject());
        msg.setText(mailDto.getText());
        mailSender.send(msg);
    }
}