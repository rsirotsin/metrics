package com.metrics.controller;

import com.metrics.dto.MetricsDto;
import com.metrics.dto.MetricsSearchDto;
import com.metrics.service.MetricsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collection;

@RestController
public class MetricsController {
    private static final Logger log = LoggerFactory.getLogger(MetricsController.class);

    public static final String ERROR_MESSAGE = "Something went wrong";

    @Autowired
    private MetricsService metricsService;

    // example http://localhost:8080/api/metrics?start=2020-12-06T01&end=2020-12-07T23
    @GetMapping("/api/metrics")
    public ResponseEntity<Collection<MetricsDto>> searchMetrics(@RequestParam(value = "start")
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH") LocalDateTime start,
                                                                @RequestParam(value = "end")
                                                                @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH") LocalDateTime end) {
        final MetricsSearchDto metricsSearchDto = new MetricsSearchDto(start, end);
        return ResponseEntity.ok(metricsService.searchMetrics(metricsSearchDto));
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception e) {
        log.error(e.getMessage(), e);
        return ERROR_MESSAGE;
    }
}