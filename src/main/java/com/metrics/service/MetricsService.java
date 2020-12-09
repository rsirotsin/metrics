package com.metrics.service;

import com.metrics.dto.MailDto;
import com.metrics.dto.MetricsDto;
import com.metrics.dto.MetricsSearchDto;
import com.metrics.properties.MetricsProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Locale;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MetricsService {
    private static final Logger log = LoggerFactory.getLogger(MetricsService.class);

    private static final String FILE_ROOT_PATH = System.getProperty("java.io.tmpdir");
    private static final DateTimeFormatter DATE_TO_SAVE_METRICS_PATH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HH" + File.separator + "mmss", Locale.ENGLISH);
    private static final DateTimeFormatter DATE_TO_READ_DIRECTORY_PATH_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy" + File.separator + "MM" + File.separator + "dd" + File.separator + "HH", Locale.ENGLISH);
    private static final String FILE_NAME_TEMPLATE = "{0}{1}";
    public static final String METRICS_FAILURE_SUBJECT = "Metrics failure";

    @Autowired
    private MetricsProperties metricsProperties;

    @Autowired
    private MailService mailService;

    public void saveMetrics(MetricsDto metricsDto) throws IOException {
        Objects.requireNonNull(metricsDto);
        final boolean isMoreThanThreshold = isMoreThanThreshold(metricsDto);
        if (isMoreThanThreshold) {
            log.error("metrics threshold error {} ", metricsDto);
            final String to = metricsProperties.getEmailAddressTo();
            final String from = metricsProperties.getEmailAddressFrom();
            final String text = metricsDto.toString();
            mailService.sendEmail(new MailDto(to, from, text, METRICS_FAILURE_SUBJECT));
            return;
        }
        saveToFileSystem(metricsDto);
    }

    public Collection<MetricsDto> searchMetrics(MetricsSearchDto metricsSearchDto) {
        Objects.requireNonNull(metricsSearchDto);
        final LocalDateTime start = metricsSearchDto.getStart();
        final LocalDateTime end = metricsSearchDto.getEnd();
        final Function<LocalDateTime, File> dateToDirectoryLambda = time -> {
            String folder = DATE_TO_READ_DIRECTORY_PATH_FORMATTER.format(time);
            String directoryPath = MessageFormat.format(FILE_NAME_TEMPLATE, FILE_ROOT_PATH, folder);
            return new File(directoryPath);
        };
        final Function<File, MetricsDto> fileToMetricsLambda = f -> {
            try {
                return fileToMetrics(f);
            } catch (IOException | ClassNotFoundException e) {
                log.error("file {}", f.toString(), e);
                return null;
            }
        };
        return Stream.iterate(start, d -> d.plusHours(1))
                .limit(ChronoUnit.HOURS.between(start, end) + 1)
                .map(dateToDirectoryLambda)
                .filter(File::exists) // directory exists
                .flatMap(f -> Stream.of(Objects.requireNonNull(f.listFiles()))) // get all metrics from directory
                .map(fileToMetricsLambda)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private MetricsDto fileToMetrics(final File f) throws IOException, ClassNotFoundException {
        try (FileInputStream fis = new FileInputStream(f);
             ObjectInputStream ios = new ObjectInputStream(fis)) {
            return (MetricsDto) ios.readObject();
        }
    }

    private void saveToFileSystem(final MetricsDto metricsDto) throws IOException {
        final File file = createFile(metricsDto.getLocalDateTime());
        try (FileOutputStream fos = new FileOutputStream(file);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(metricsDto);
        }
    }

    private File createFile(LocalDateTime now) throws IOException {
        final String folder = DATE_TO_SAVE_METRICS_PATH_FORMATTER.format(now);
        final String fileName = MessageFormat.format(FILE_NAME_TEMPLATE, FILE_ROOT_PATH, folder);
        final File file = new File(fileName);
        file.getParentFile().mkdirs();
        file.createNewFile();
        return file;
    }

    private boolean isMoreThanThreshold(MetricsDto metricsDto) {
        return metricsDto.getSystemCpuLoadPercentage() > metricsProperties.getCpuLoadThreshold()
                || metricsDto.getDiskUsagePercentage() > metricsProperties.getDiskThreshold()
                || metricsDto.getMemoryPercentage() > metricsProperties.getMemoryThreshold();
    }
}