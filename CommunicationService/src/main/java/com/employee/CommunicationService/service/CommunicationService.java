package com.employee.CommunicationService.service;

import com.employee.CommunicationService.dto.request.*;
import com.employee.CommunicationService.dto.response.ApiResponse;
import com.employee.CommunicationService.dto.response.SingleResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface CommunicationService {

    void sendAccountCreatedEmail(String emailId);

    SingleResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes);

    SingleResponse<String> sendCompletedRegistrationEmail(String emailId);

    SingleResponse<String> sendLeaveEmail(LeaveEmailRequest request);

    void sendConfirmationLeaveEmail(LeaveConfirmationRequest request);

    void sendInterviewEmail(InterviewRequest request);

    void sendLeaveApprovedEmail(LeaveApproveRequest request);

    void sendLeaveRejectedEmail(LeaveRejectedRequest request);
}
