package com.metrics;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import com.metrics.dto.MetricsDto;
import com.metrics.dto.MetricsSearchDto;
import com.metrics.job.MetricsJob;
import com.metrics.properties.MetricsProperties;
import com.metrics.service.MetricsService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@MockBean(MetricsJob.class) // to disable job
public class MetricsServiceTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetup.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withDisabledAuthentication());

    @Autowired
    private MetricsService metricsService;

    @Autowired
    private MetricsProperties metricsProperties;

    @TempDir
    static Path sharedTempDir;

    @BeforeAll
    static void setTmpDir() {
        System.setProperty("java.io.tmpdir", sharedTempDir.toString() + "\\");
    }

    @Test
    public void shouldSendEmailAfterThresholdOvercoming() throws IOException, MessagingException {
        final MetricsDto metricsDto = new MetricsDto(100, 100, 100, LocalDateTime.now());
        metricsService.saveMetrics(metricsDto);
        greenMail.waitForIncomingEmail(5000, 1);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertThat(receivedMessages.length).isEqualTo(1);

        MimeMessage receivedMessage = receivedMessages[0];
        assertEquals(metricsDto.toString(), GreenMailUtil.getBody(receivedMessage));
        assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(metricsProperties.getEmailAddressTo());
        assertThat(receivedMessage.getFrom()[0].toString()).isEqualTo(metricsProperties.getEmailAddressFrom());
        assertThat(receivedMessage.getSubject()).isEqualTo(MetricsService.METRICS_FAILURE_SUBJECT);
    }

    @Test
    public void shouldSaveAndFindMetrics() throws IOException {
        final Collection<MetricsDto> metrics = metrics();
        for (MetricsDto metric : metrics) {
            metricsService.saveMetrics(metric);
        }
        // add one more and this metrics is not valid for the search
        metricsService.saveMetrics(new MetricsDto(3, 25, 60, LocalDateTime.parse("2020-12-12T07", formatter)));
        assertEquals(metrics, metricsService.searchMetrics(getMetricsSearchDto()));
    }

    private MetricsSearchDto getMetricsSearchDto() {
        return new MetricsSearchDto(LocalDateTime.parse("2020-12-04T06", formatter), LocalDateTime.parse("2020-12-04T07", formatter));
    }

    @Test
    public void shouldThrowNullPointerException() throws IOException {
        assertThrows(NullPointerException.class, () -> metricsService.saveMetrics(null));
        assertThrows(NullPointerException.class, () -> metricsService.searchMetrics(null));
    }

    private Collection<MetricsDto> metrics() {
        return new ArrayList<>(Arrays.asList(
                new MetricsDto(0, 26, 68, LocalDateTime.parse("2020-12-04T06", formatter)),
                new MetricsDto(3, 25, 60, LocalDateTime.parse("2020-12-04T07", formatter))));
    }
}