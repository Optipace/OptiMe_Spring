package com.employee.AuthService.client;

import com.employee.AuthService.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "NOTIFICATION-SERVICE")
public interface CommunicationClient {
    @PostMapping("/api/communication/email/sendNewOtpToEmail")
    public ApiResponse<String> sendNewOtpToEmail(@RequestParam("emailId") String emailId, @RequestParam("otp") String otp, @RequestParam("expiryMinutes") Long otpExpiryMinutes);

    @PostMapping("/api/communication/email/sendCompletedRegistrationEmail")
    public ApiResponse<String> sendCompleteRegisteredEmail(@RequestParam("emailId") String emailId);
}
