package com.employee.NotificationService.serviceImpl;

import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.dto.response.ApiResponse;
import com.employee.NotificationService.service.CommunicationService;
import com.employee.NotificationService.service.EmailService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@Slf4j
public class CommunicationServiceImpl implements CommunicationService {

    private final EmailService emailService;
    private final TemplateEngine templateEngine;


    @Override
    public void sendAccountCreatedEmail(String emailId) {
        String registrationUrl = "http://localhost:/completedregistration"; // Put final registration url here
        Context context = new Context();
        context.setVariable("emailId",emailId);
        context.setVariable("registrationUrl", registrationUrl);

        String htmlBody = templateEngine.process("AccountCreationTemplate", context);
        String subject = "Welcome to Optipace Technologies";
        try {
            emailService.sendHtmlEmail(emailId, subject, htmlBody);
            log.info("Email sent to {}",emailId);
        } catch (Exception e) {
            log.error("Email sending failed for {}",emailId);
        }
    }

    @Override
    public ApiResponse<String> sendNewOtpToEmail(String emailId, String otp, Long otpExpiryMinutes) {
        // 1. Variable for the HTML template
        Context context = new Context();
        context.setVariable("otpCode", otp);
        context.setVariable("validMinutes", otpExpiryMinutes);

        // 2. Process the HTML file (points to src/main/resources/templates/OtpEmailTemplate.html)
        String htmlBody = templateEngine.process("OtpEmailTemplate", context);
        String subject = "Welcome to Optipace Technologies";

        log.info("Communication service : Sending email {}", emailId);
        return emailService.sendHtmlEmail(emailId, subject, htmlBody);
    }

    @Override
    public ApiResponse<String> sendCompletedRegistrationEmail(String emailId){
        String loginUrl = "http:login.optipace.com"; // Put final login url here
        // 1. Variable for the HTML template
        Context context = new Context();
        context.setVariable("loginUrl", loginUrl);

        // 2. Process the HTML file (points to src/main/resources/templates/RegistrationCompletionTemplate.html)
        String htmlBody = templateEngine.process("RegistrationCompletionTemplate", context);
        String subject = "Welcome to Optipace Technologies";

        log.info("Communication service: Sending completed registration email to {}",emailId);
        return emailService.sendHtmlEmail(emailId, subject, htmlBody);
    }

    @Override
    public void sendLeaveEmail(LeaveEmailRequest request) {
        Context context = new Context();
//        TODO : Must be completed
    }
}