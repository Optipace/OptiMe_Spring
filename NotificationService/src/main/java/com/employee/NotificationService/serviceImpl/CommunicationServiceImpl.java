package com.employee.NotificationService.serviceImpl;

import com.employee.NotificationService.dto.request.InterviewRequest;
import com.employee.NotificationService.dto.request.LeaveConfirmationRequest;
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
    public ApiResponse<String> sendLeaveEmail(LeaveEmailRequest request) {
        Context context = new Context();
        context.setVariable("managerName", request.getApproverName());
        context.setVariable("employeeName", request.getApplicantName());
        context.setVariable("employeeId", request.getApplicantEmployeeId());
        context.setVariable("leaveType", request.getLeaveType());
        context.setVariable("fromDate", request.getFromDate());
        context.setVariable("toDate", request.getToDate());
        context.setVariable("reason", request.getReason());

        String htmlBody = templateEngine.process("LeaveTemplate", context);
        String subject = "Leave Request";

        log.info("Communication Service: Sending Leave requesting email to {}",request.getApproverEmailId());
        return emailService.sendHtmlEmail(request.getApproverEmailId(), subject, htmlBody);
    }

    @Override
    public void sendConfirmationLeaveEmail(LeaveConfirmationRequest request) {
        Context context = new Context();
        context.setVariable("employeeName", request.getApplicantName());
        context.setVariable("leaveType", request.getLeaveType());
        context.setVariable("fromDate", request.getFromDate());
        context.setVariable("toDate", request.getToDate());

        String htmlBody = templateEngine.process("LeaveConfirmationTemplate", context);
        String subject = "Leave Application submitted";

        log.info("Communication Service: Sending confirmation leave email to {}",request.getApplicantEmailId());
        emailService.sendHtmlEmail(request.getApplicantEmailId(), subject, htmlBody);
    }

    @Override
    public void sendInterviewEmail(InterviewRequest request) {
        Context context = new Context();
        context.setVariable("formUrl", request.getUrl());
        context.setVariable("token", request.getToken());
        context.setVariable("candidateEmail", request.getEmailId());

        String htmlBody = templateEngine.process("InterviewFormTemplate", context);
        String subject = "Welcome to Optipace Technologies";

        log.info("Communication Service: Sending basic details form email to {}", request.getEmailId());
        emailService.sendHtmlEmail(request.getEmailId(), subject, htmlBody);
    }
}