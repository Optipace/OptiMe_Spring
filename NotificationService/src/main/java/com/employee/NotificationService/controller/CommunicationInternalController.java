package com.employee.NotificationService.controller;

import com.employee.NotificationService.dto.request.LeaveConfirmationRequest;
import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.dto.response.ApiResponse;
import com.employee.NotificationService.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communication/email")
@RequiredArgsConstructor
public class CommunicationInternalController {

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
}
