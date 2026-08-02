package com.employee.CommunicationService.service;

import com.employee.CommunicationService.dto.request.InterviewRequest;
import com.employee.CommunicationService.dto.request.LeaveConfirmationRequest;
import com.employee.CommunicationService.dto.request.LeaveEmailRequest;
import com.employee.CommunicationService.dto.response.ApiResponse;

public interface CommunicationService {

    void sendAccountCreatedEmail(String emailId);

    ApiResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes);

    ApiResponse<String> sendCompletedRegistrationEmail(String emailId);

    ApiResponse<String> sendLeaveEmail(LeaveEmailRequest request);

    void sendConfirmationLeaveEmail(LeaveConfirmationRequest request);

    void sendInterviewEmail(InterviewRequest request);
}
