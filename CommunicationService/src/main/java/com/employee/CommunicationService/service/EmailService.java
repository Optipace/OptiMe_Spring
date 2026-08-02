package com.employee.CommunicationService.service;

import com.employee.CommunicationService.dto.response.ApiResponse;

public interface EmailService {
    ApiResponse<String> sendHtmlEmail(String to, String subject, String body);
}
