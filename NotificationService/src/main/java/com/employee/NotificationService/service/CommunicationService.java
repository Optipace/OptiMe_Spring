package com.employee.NotificationService.service;

import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.dto.response.ApiResponse;

public interface CommunicationService {

    void sendAccountCreatedEmail(String emailId);

    ApiResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes);

    ApiResponse<String> sendCompletedRegistrationEmail(String emailId);

    void sendLeaveEmail(LeaveEmailRequest request);
}
