package com.employee.CommunicationService.controller;

import com.employee.CommunicationService.dto.request.InterviewRequest;
import com.employee.CommunicationService.dto.request.LeaveConfirmationRequest;
import com.employee.CommunicationService.dto.request.LeaveEmailRequest;
import com.employee.CommunicationService.dto.response.ApiResponse;
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
    public ResponseEntity<String> sendAccountCreatedEmail(@RequestParam("emailId") String emailId){
        communicationService.sendAccountCreatedEmail(emailId);
        return ResponseEntity.ok("Email processed");
    }

    @PostMapping("/sendNewOtpToEmail")
    public ApiResponse<String> sendNewOtpToEmail(@RequestParam("emailId") String emailId, @RequestParam("otp") String otp, @RequestParam("expiryMinutes") Long otpExpiryMinutes){
        return communicationService.sendNewOtpToEmail(emailId, otp, otpExpiryMinutes);
    }

    @PostMapping("/sendCompletedRegistrationEmail")
    public ApiResponse<String> sendCompletedRegistrationEmail(@RequestParam("emailId") String emailId){
        return communicationService.sendCompletedRegistrationEmail(emailId);
    }

    @PostMapping("/sendLeaveEmail")
    public ApiResponse<String> sendLeaveEmail(@RequestBody LeaveEmailRequest request){
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
}
