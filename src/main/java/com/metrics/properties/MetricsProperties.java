package com.metrics.properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MetricsProperties {
    @Value("${cpu.load.threshold:0}")
    private int cpuLoadThreshold;

    @Value("${disk.threshold:0}")
    private int diskThreshold;

    @Value("${memory.threshold:0}")
    private int memoryThreshold;

    @Value("${email.address.to.threshold}")
    private String emailAddressTo;

    @Value("${email.address.from.threshold}")
    private String emailAddressFrom;

    public int getCpuLoadThreshold() {
        return cpuLoadThreshold;
    }

    public int getDiskThreshold() {
        return diskThreshold;
    }

    public int getMemoryThreshold() {
        return memoryThreshold;
    }

    public String getEmailAddressTo() {
        return emailAddressTo;
    }

    public String getEmailAddressFrom() {
        return emailAddressFrom;
    }

    @Override
    public String toString() {
        return "MetricsProperties{" +
                "cpuLoadThreshold=" + cpuLoadThreshold +
                ", diskThreshold=" + diskThreshold +
                ", memoryThreshold=" + memoryThreshold +
                ", emailAddressTo='" + emailAddressTo + '\'' +
                ", emailAddressFrom='" + emailAddressFrom + '\'' +
                '}';
    }
}