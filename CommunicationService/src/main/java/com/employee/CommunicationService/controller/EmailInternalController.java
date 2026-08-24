package com.employee.CommunicationService.controller;

import com.employee.CommunicationService.dto.request.*;
import com.employee.CommunicationService.dto.response.ApiResponse;
import com.employee.CommunicationService.dto.response.SingleResponse;
import com.employee.CommunicationService.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication/email")
@RequiredArgsConstructor
public class EmailInternalController {

    private final CommunicationService communicationService;

    @PostMapping("/sendWelcomeEmail")
    public ResponseEntity<String> sendAccountCreatedEmail(@RequestParam("emailId") String emailId, @RequestParam("contact") String contact){
        communicationService.sendAccountCreatedEmail(emailId, contact);
        return ResponseEntity.ok("Email processed");
    }

    @PostMapping("/sendNewOtpToEmail")
    public SingleResponse<String> sendNewOtpToEmail(@RequestParam("emailId") String emailId, @RequestParam("otp") String otp, @RequestParam("expiryMinutes") Long otpExpiryMinutes){
        return communicationService.sendNewOtpToEmail(emailId, otp, otpExpiryMinutes);
    }

    @PostMapping("/sendCompletedRegistrationEmail")
    public SingleResponse<String> sendCompletedRegistrationEmail(@RequestParam("emailId") String emailId){
        return communicationService.sendCompletedRegistrationEmail(emailId);
    }

    @PostMapping("/sendLeaveEmail")
    public SingleResponse<String> sendLeaveEmail(@RequestBody LeaveEmailRequest request){
        return communicationService.sendLeaveEmail(request);
    }

    @PostMapping("/sendConfirmLeaveEmail")
    void sendConfirmationLeaveEmail(@RequestBody LeaveConfirmationRequest request){
        communicationService.sendConfirmationLeaveEmail(request);
    }

    @PostMapping("/interviewEmail")
    void sendInterviewEmail(@RequestBody InterviewRequest request){
        communicationService.sendInterviewEmail(request);
    }

    @PostMapping("/sendLeaveApprovedEmail")
    void sendLeaveApprovedEmail(@RequestBody LeaveApproveRequest request){
        communicationService.sendLeaveApprovedEmail(request);
    }

    @PostMapping("/sendLeaveRejectedEmail")
    void sendLeaveRejectedEmail(@RequestBody LeaveRejectedRequest request){
        communicationService.sendLeaveRejectedEmail(request);
    }
}
