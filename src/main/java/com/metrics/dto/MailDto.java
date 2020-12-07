package com.metrics.dto;

public class MailDto {
    private final String to;
    private final String from;
    private final String text;
    private final String subject;

    public MailDto(String to, String from, String text, String subject) {
        this.to = to;
        this.from = from;
        this.text = text;
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "MailDto{" +
                "to='" + to + '\'' +
                ", from='" + from + '\'' +
                ", text='" + text + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }
}