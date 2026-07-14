package com.employee.NotificationService.service;

import com.employee.NotificationService.dto.response.ApiResponse;

public interface EmailService {
    ApiResponse<String> sendHtmlEmail(String to, String subject, String body);
}
