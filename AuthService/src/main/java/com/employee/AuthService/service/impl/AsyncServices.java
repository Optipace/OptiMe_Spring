package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.CommunicationClient;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.enums.CustomStatus;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.UserOtp;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncServices {

    private static final long OTP_EXPIRY_MINUTES = 5;

    private final CommunicationClient communicationClient;

    @Async("generateOtpTask")
    public void asyncSentOtpClientCall(UserOtp userOtp){
        String message = "Otp sent successfully";
        try {
            log.info("Calling Email Service to send new otp");
            SingleResponse<String> apiResponse = communicationClient.sendNewOtpToEmail(
                    userOtp.getEmailId(), userOtp.getEmailOtp(), OTP_EXPIRY_MINUTES);
            log.info("Email service called");

            if(apiResponse != null && apiResponse.getStatusCode() == 200) {
                message = apiResponse.getMessage();
                // Call the SMS client here for userOtp.getMobileOtp()
            }
        } catch (FeignException e) {
            log.error("Email service failed",e);
            throw new CustomException(null, CustomStatus.EMAIL_SENDING_FAILED, 409);
        }
    }

    @Async("completeRegistration")
    public void asyncCompleteRegistration(String emailId){
        String message = "Email sent";
        try{
            log.info("Calling email service");
            SingleResponse<String> apiResponse = communicationClient.sendCompleteRegisteredEmail(emailId);
            log.info("Email service called to send completed registration email");

            if(apiResponse != null && apiResponse.getStatusCode() == 200){
                message = apiResponse.getMessage();
            }
        }catch (FeignException e){
            log.warn("Failed to completed registration email", e);
        }
    }
}
