package com.employee.NotificationService.service;

import com.employee.NotificationService.dto.request.InterviewRequest;
import com.employee.NotificationService.dto.request.LeaveConfirmationRequest;
import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommunicationService {

    void sendAccountCreatedEmail(String emailId);

    ApiResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes);

    ApiResponse<String> sendCompletedRegistrationEmail(String emailId);

    ApiResponse<String> sendLeaveEmail(LeaveEmailRequest request);

    void sendConfirmationLeaveEmail(LeaveConfirmationRequest request);

    void sendInterviewEmail(InterviewRequest request);
}
