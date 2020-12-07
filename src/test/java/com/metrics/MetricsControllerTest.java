package com.metrics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import com.metrics.controller.MetricsController;
import com.metrics.dto.MetricsDto;
import com.metrics.job.MetricsJob;
import com.metrics.service.MetricsService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@MockBean(MetricsJob.class) // to disable job
@AutoConfigureMockMvc
public class MetricsControllerTest {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH");

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MetricsService metricsService;

    @Test
    public void shouldFindMetrics() throws Exception {
        when(metricsService.searchMetrics(notNull())).thenReturn(metrics());

        mvc.perform(MockMvcRequestBuilders.get("/api/metrics")
                .param("start", "2020-12-03T10")
                .param("end", "2020-12-04T11")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(equalTo("[{\"systemCpuLoadPercentage\":0,\"memoryPercentage\":26,\"diskUsagePercentage\":68,\"localDateTime\":\"2020-12-04T07:00:00\"},{\"systemCpuLoadPercentage\":3,\"memoryPercentage\":25,\"diskUsagePercentage\":60,\"localDateTime\":\"2020-12-04T06:00:00\"}]")));
    }

    @Test
    public void shouldHandleException() throws Exception {
        when(metricsService.searchMetrics(notNull())).thenThrow(RuntimeException.class);

        mvc.perform(MockMvcRequestBuilders.get("/api/metrics")
                .param("start", "2020-12-03T10")
                .param("end", "2020-12-04T11")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string(equalTo(MetricsController.ERROR_MESSAGE)));
    }

    private List<MetricsDto> metrics() {
        return new ArrayList<>(Arrays.asList(
                new MetricsDto(0, 26, 68, LocalDateTime.parse("2020-12-04T07", formatter)),
                new MetricsDto(3, 25, 60, LocalDateTime.parse("2020-12-04T06", formatter))));
    }
}