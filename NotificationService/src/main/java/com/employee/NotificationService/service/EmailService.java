package com.employee.NotificationService.service;

public interface EmailService {
    void sendHtmlEmail(String to, String subject, String body);
}
