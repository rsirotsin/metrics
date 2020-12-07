package com.metrics.job;

import com.metrics.dto.MetricsDto;
import com.metrics.service.MetricsService;
import com.sun.management.OperatingSystemMXBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.FileStore;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Component
public class MetricsJob {
    private static final Logger log = LoggerFactory.getLogger(MetricsJob.class);

    private final OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();

    @Autowired
    private MetricsService metricsService;

    /**
     * 1 minute = 60000 milliseconds
     */
    @Scheduled(fixedRate = 60000)
    public void generateMetrics() {
        try {
            final int systemCpuLoadPercentage = getCpuUsagePercentage();
            final int memoryPercentage = getMemoryUsagePercentage();
            final int diskUsagePercentage = getDiskUsagePercentage();
            log.info("system cpu load {} %, memory usage {} %, disk usage {} %", systemCpuLoadPercentage, memoryPercentage, diskUsagePercentage);
            metricsService.saveMetrics(new MetricsDto(systemCpuLoadPercentage, memoryPercentage, diskUsagePercentage, LocalDateTime.now(ZoneId.of("UTC"))));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private int getCpuUsagePercentage() {
        final int process = Runtime.getRuntime().availableProcessors();
        final double systemCpuLoad = operatingSystemMXBean.getSystemCpuLoad();
        return (int) (process * systemCpuLoad * 100);
    }

    private int getMemoryUsagePercentage() {
        final long freePhysicalMemorySize = operatingSystemMXBean.getFreePhysicalMemorySize();
        final long totalPhysicalMemorySize = operatingSystemMXBean.getTotalPhysicalMemorySize();
        final long usedMemorySize = totalPhysicalMemorySize - freePhysicalMemorySize;
        final BigDecimal usedMemory = new BigDecimal(usedMemorySize);
        final BigDecimal totalMemory = new BigDecimal(totalPhysicalMemorySize);
        final BigDecimal divide = usedMemory.divide(totalMemory, 2, RoundingMode.HALF_UP);
        return (int) (divide.doubleValue() * 100);
    }

    private int getDiskUsagePercentage() {
        BigDecimal sumUsableSpace = new BigDecimal(0);
        BigDecimal sumTotalSpace = new BigDecimal(0);
        for (Path root : FileSystems.getDefault().getRootDirectories()) {
            try {
                FileStore store = Files.getFileStore(root);
                long usableSpace = store.getUsableSpace();
                long totalSpace = store.getTotalSpace();
                sumUsableSpace = sumUsableSpace.add(new BigDecimal(usableSpace));
                sumTotalSpace = sumTotalSpace.add(new BigDecimal(totalSpace));
            } catch (IOException e) {
                // NOP
            }
        }
        final BigDecimal result = sumUsableSpace.divide(sumTotalSpace, 2, RoundingMode.HALF_UP);
        return (int) (result.doubleValue() * 100);
    }
}