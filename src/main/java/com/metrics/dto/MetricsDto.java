package com.metrics.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class MetricsDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private final int systemCpuLoadPercentage;
    private final int memoryPercentage;
    private final int diskUsagePercentage;
    private final LocalDateTime time;

    public MetricsDto(int systemCpuLoadPercentage, int memoryPercentage, int diskUsagePercentage, LocalDateTime time) {
        this.systemCpuLoadPercentage = systemCpuLoadPercentage;
        this.memoryPercentage = memoryPercentage;
        this.diskUsagePercentage = diskUsagePercentage;
        this.time = time;
    }

    public int getSystemCpuLoadPercentage() {
        return systemCpuLoadPercentage;
    }

    public int getMemoryPercentage() {
        return memoryPercentage;
    }

    public int getDiskUsagePercentage() {
        return diskUsagePercentage;
    }

    public LocalDateTime getLocalDateTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetricsDto that = (MetricsDto) o;
        return systemCpuLoadPercentage == that.systemCpuLoadPercentage &&
                memoryPercentage == that.memoryPercentage &&
                diskUsagePercentage == that.diskUsagePercentage &&
                Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(systemCpuLoadPercentage, memoryPercentage, diskUsagePercentage, time);
    }

    @Override
    public String toString() {
        return "{" +
                "systemCpuLoadPercentage=" + systemCpuLoadPercentage +
                ", memoryPercentage=" + memoryPercentage +
                ", diskUsagePercentage=" + diskUsagePercentage +
                ", time=" + time +
                '}';
    }
}