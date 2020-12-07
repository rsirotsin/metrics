package com.metrics;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.metrics.dto.MailDto;
import com.metrics.job.MetricsJob;
import com.metrics.service.MailService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.internet.MimeMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
public class MailServiceTest {
    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private MailService mailService;

    @Test
    public void shouldSendEmail(){
        // Given
        String to = "to@mail.ru";
        String from = "from@mail.ru";
        String title = "Title";
        String text = "Content";

        // When
        mailService.sendEmail(new MailDto(to, from, text, title));
        greenMail.waitForIncomingEmail(5000, 1);
        // Then
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages.length).isEqualTo(1);
        assertEquals("Content", GreenMailUtil.getBody(receivedMessages[0]));
    }
}