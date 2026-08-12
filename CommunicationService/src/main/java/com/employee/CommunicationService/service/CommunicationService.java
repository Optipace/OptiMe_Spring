package com.employee.CommunicationService.service;

import com.employee.CommunicationService.dto.request.*;
import com.employee.CommunicationService.dto.response.ApiResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommunicationService {

    void sendAccountCreatedEmail(String emailId);

    ApiResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes);

    ApiResponse<String> sendCompletedRegistrationEmail(String emailId);

    ApiResponse<String> sendLeaveEmail(LeaveEmailRequest request);

    void sendConfirmationLeaveEmail(LeaveConfirmationRequest request);

    void sendInterviewEmail(InterviewRequest request);

    void sendLeaveApprovedEmail(LeaveApproveRequest request);

    void sendLeaveRejectedEmail(LeaveRejectedRequest request);
}
