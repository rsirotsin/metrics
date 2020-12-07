package com.metrics.dto;

import java.time.LocalDateTime;

public class MetricsSearchDto {
    private final LocalDateTime start;
    private final LocalDateTime end;

    public MetricsSearchDto(LocalDateTime start, LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "MetricsSearchDto{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}