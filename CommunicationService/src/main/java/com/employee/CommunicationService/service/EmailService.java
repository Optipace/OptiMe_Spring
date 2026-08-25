package com.employee.CommunicationService.service;

import com.employee.CommunicationService.dto.response.ApiResponse;
import com.employee.CommunicationService.dto.response.SingleResponse;

public interface EmailService {
    SingleResponse<String> sendHtmlEmail(String to, String subject, String body);
}
