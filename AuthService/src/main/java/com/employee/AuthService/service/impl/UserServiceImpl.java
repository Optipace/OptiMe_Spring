package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.AdminClient;
import com.employee.AuthService.client.CommunicationClient;
import com.employee.AuthService.client.EmployeeClient;
import com.employee.AuthService.config.AppProperties;
import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.*;
import com.employee.AuthService.enums.*;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.*;
import com.employee.AuthService.repository.*;
import com.employee.AuthService.service.*;
import com.employee.AuthService.util.JwtUtil;
import feign.FeignException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final PasswordRepository passwordRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final EmployeeClient employeeClient;
    private final ObjectMapper objectMapper;
    private static final long OTP_GENERATE_COUNT= 3;
    private static final long OTP_RETRY_COUNT= 3;
    private static final long OTP_LOCK_DURATION_MINUTES = 60;
    private static final long OTP_EXPIRY_MINUTES = 5;
    private final AppProperties appProperties;
    private final CommunicationClient communicationClient;
    private final AdminClient adminClient;

    @Override
    @Transactional
    public ApiResponse<?> generateOtp(OtpRequest request) {

        boolean isAlreadyUser = userRepository.findByEmailId(request.getEmailId()).isPresent()
                && userRepository.findByContact(request.getContact()).isPresent();

        if (!isAlreadyUser) {
            throw new CustomException("Please register this email or contact number in office!.", HttpStatus.CONFLICT);
        }

        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseGet(() -> {
                    UserOtp newOtp = new UserOtp();
                    newOtp.setEmailId(request.getEmailId());
                    newOtp.setContact(request.getContact());
                    return newOtp;
                });

        if(userOtp.getId() != null){
            if(userOtp.getOtpCount() >= OTP_GENERATE_COUNT){
                LocalDateTime lockTime = userOtp.getUpdatedOn();
                LocalDateTime unlockTime =lockTime.plusMinutes(OTP_LOCK_DURATION_MINUTES);
                if(LocalDateTime.now().isBefore(unlockTime)){
                    Duration remaining = Duration.between(LocalDateTime.now(), unlockTime);

                    long minutes = remaining.toMinutes();
                    long seconds = remaining.minusMinutes(minutes).getSeconds();
                    throw new CustomException("OTP generation limit exceeded. Try again in " + minutes + " min " + seconds + " sec",
                            HttpStatus.BAD_REQUEST);
                }
                // Unlock after 1 hour
                userOtp.setRetryCount(0);
            }
        }

        userOtp.setRegisterStatus(RegisterEnum.N); // OTP Register status to N (NO)
        userOtp.setAvailable(RegisterEnum.Y); // Otp available status to Y (Not expired fresh otp)
        userOtp.setEmailOtp(String.valueOf(new Random().nextInt(899999) + 100000));
        userOtp.setMobileOtp(String.valueOf(new Random().nextInt(899999) + 100000));
        userOtp.setRetryCount(0);
        userOtp.setOtpCount(userOtp.getOtpCount() + 1);
        userOtpRepository.save(userOtp);
        String message = "Otp sent successfully";
        try {
            log.info("Calling Email Service to send new otp");
            ApiResponse<String> apiResponse = communicationClient.sendNewOtpToEmail(userOtp.getEmailId(), userOtp.getEmailOtp(), OTP_EXPIRY_MINUTES);
            log.info("Email service called");

            if(apiResponse != null && apiResponse.getStatusCode() == 200) {
                message = apiResponse.getMessage();
                // Call the SMS client here for userOtp.getMobileOtp()
            }
        } catch (FeignException e) {
            log.error("Email service failed",e);
            throw new CustomException("Something went wrong! Error while sending email\nPlease try again", HttpStatus.INTERNAL_SERVER_ERROR);
//            return new ApiResponse<>(
//                    false,
//                    "Something went wrong! Error while sending email\nPlease try again",
//                    null,
//                    LocalDateTime.now(),
//                    500
//            );
        }
        return new ApiResponse<>(
                true,
                message,
                null,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<ValidationResponse> validateOtp(ValidationRequest request) {
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException("Otp not found", HttpStatus.NOT_FOUND));

        // Check otp is already registered or not
        if (userOtp.getRegisterStatus() == RegisterEnum.Y) {
            throw new CustomException("OTP already verified", HttpStatus.CONFLICT);
        }

        boolean isEmailOtpInvalid = !(userOtp.getEmailOtp().equals(request.getEmailOtp()) || request.getEmailOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));
        boolean isMobileOtpInvalid = !(userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));

        LocalDateTime expiryTime = userOtp.getUpdatedOn().plusMinutes(OTP_EXPIRY_MINUTES);
        if (expiryTime.isBefore(LocalDateTime.now()) || userOtp.getAvailable().equals(RegisterEnum.N)) {
            userOtp.setAvailable(RegisterEnum.N);
            userOtpRepository.save(userOtp);
            throw new CustomException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if(userOtp.getRetryCount() >= OTP_RETRY_COUNT){
            throw new CustomException("OTP retry limit exceeded. Please generate new otp", HttpStatus.BAD_REQUEST);
        }

        if(isEmailOtpInvalid && isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Both Email and Mobile OTPs are invalid", HttpStatus.BAD_REQUEST);
        }
        if (isEmailOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Invalid Email OTP", HttpStatus.BAD_REQUEST);
        }
        if (isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Invalid Mobile OTP", HttpStatus.BAD_REQUEST);
        }

        userOtp.setAvailable(RegisterEnum.N);       // Expire the otp
        userOtp.setRegisterStatus(RegisterEnum.Y);  // Employee OTP registered successfully
        userOtp.setValidated(StatusEnum.F); // Set validation token to False
        userOtp.setOtpCount(0);
        userOtp.setValidationToken(UUID.randomUUID().toString()); // Random validation token generation
        userOtpRepository.saveAndFlush(userOtp);

        User user = userRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException("Employee with this email or contact not found", HttpStatus.NOT_FOUND));

        EmployeeResponse response = null;

        try {

            ApiResponse<EmployeeResponse> apiResponse = employeeClient.getProfile(user.getEmployeeId());
            log.info("Employee Client called");

            if (apiResponse != null && apiResponse.getData() != null) {
                response = apiResponse.getData();
            }

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            // Resolve status code safely.
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (e.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(e.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        ValidationResponse validationResponse = new ValidationResponse(response, userOtp.getValidationToken());
        return new ApiResponse<>(
                true,
                "OTP validated successfully",
                validationResponse,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> completeRegistration(CompleteRegisterRequest request) {

        User user = userRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("Employee ID not found", HttpStatus.NOT_FOUND));

        UserOtp userOtp = userOtpRepository.findByEmailIdOrContact(user.getEmailId(), user.getContact())
                .orElseThrow(() -> new CustomException("Validated Email-Id or contact not found OR check provided employee Id", HttpStatus.NOT_FOUND));

        if (!userOtp.getValidationToken().equals(request.getValidationToken())) {
            throw new CustomException("Token not found. Invalid user!", HttpStatus.BAD_REQUEST);
        }
//        userRepository.findByEmployeeId(request.getEmployeeId())
//                .ifPresent(u -> {throw new CustomException("Employee ID must be unique", HttpStatus.CONFLICT);});

        if (userOtp.getRegisterStatus() != RegisterEnum.Y) {
            throw new CustomException("OTP has not been validated for this user", HttpStatus.BAD_REQUEST);
        }

        // Prevent Duplicate Registration (Fixes the User ID already exists crash)
        boolean isAlreadyRegistered = passwordRepository.existsByUserId(user.getId());
        if (isAlreadyRegistered) {
            throw new CustomException(
                    "Provided employee Id is already fully registered. If not please provide your correct employee Id",
                    HttpStatus.BAD_REQUEST);
        }

        // Check if personal email is already claimed by someone else
        boolean isEmailTaken = userRepository.existsByPersonalEmailAndEmployeeIdNot(
                request.getPersonalEmail(), request.getEmployeeId());
        if (isEmailTaken) {
            throw new CustomException(
                    "The personal email provided is already registered to another account.",
                    HttpStatus.BAD_REQUEST);
        }

        Password password = new Password();
        password.setPassword(passwordEncoder.encode(request.getPassword()));
        password.setUser(user);

        user.setPassword(password);

        user.setPersonalEmail(request.getPersonalEmail());
        userOtp.setValidated(StatusEnum.T); // Set validation token to true (T)
        passwordRepository.save(password);
        userRepository.save(user);

        EmployeeProfilePayload profilePayload = new EmployeeProfilePayload(
                request.getEmployeeId(),
                request.getCurrentAddress(),
                request.getEmergencyContact(),
                request.getBloodGroup()
        );

        try {
            employeeClient.completeProfile(profilePayload);

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asString();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }
//        userOtpRepository.delete(userOtp);
        String message = "Email sent";
        try{
            log.info("Calling email service");
            ApiResponse<String> apiResponse = communicationClient.sendCompleteRegisteredEmail(user.getEmailId());
            log.info("Email service called to send completed registration email");

            if(apiResponse != null && apiResponse.getStatusCode() == 200){
                message = apiResponse.getMessage();
            }
        }catch (FeignException e){
            log.warn("Failed to completed registration email", e);

            return new ApiResponse<>(
                    false,
                    "Failed to send email",
                    null,
                    LocalDateTime.now(),
                    500
            );
        }

//        try {
//            emailService.sendHtmlEmail(user.getEmailId(), subject, htmlBody);
//        } catch (Exception e) {
//            log.error("Email sending failed", e);
//        }
        return new ApiResponse<>(
                true,
                "Registered successfully \n " +message,
                null,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }

    public ApiResponse<LoginResponse> login(LoginRequest request) {

        User user = userRepository.findByEmailIdOrContact(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new CustomException("Employee not found", HttpStatus.NOT_FOUND));

        if (user.getPassword() == null) {
            throw new CustomException("Invalid password", HttpStatus.BAD_REQUEST);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword().getPassword()) || request.getPassword() == null || user.getPassword() == null) {
            throw new CustomException("Invalid Password", HttpStatus.BAD_REQUEST);
        }

        String accessToken = jwtUtil.generateToken(user.getUserName(), user.getContact(), user.getEmailId(), user.getEmployeeId(), String.valueOf(user.getRole()));
        String refreshToken = refreshTokenService.create(user);

        if (user.getUserStatus() == null || user.getUserStatus() == UserStatusEnum.INACTIVE) {
            user.setUserStatus(UserStatusEnum.ACTIVE);
            userRepository.save(user);
        }

        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);

        return new ApiResponse<>(
                true,
                "Login successful",
                loginResponse,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }

    @Override
    public ApiResponse<?> getMasterDetails() {

        ApiResponse<List<OfficeResponse>> officeResponse = adminClient.getOfficeList();
        ApiResponse<MasterResponse> empResponse = employeeClient.getMasterDetails();

        MasterResponse masterResponse = (empResponse != null && empResponse.getData() != null)
                ? empResponse.getData()
                : new MasterResponse();

        if (officeResponse != null && officeResponse.getData() != null) {
            masterResponse.setOfficeResponse(officeResponse.getData());
        }
        return new ApiResponse<>(
                true,
                "Master Response",
                masterResponse,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<?> resetPassword(ResetPasswordRequest request) {
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseThrow(() -> new CustomException("Email Id or Contact not found", HttpStatus.NOT_FOUND));

        // Check otp is already registered or not
        if (userOtp.getRegisterStatus() == RegisterEnum.Y) {
            throw new CustomException("OTP already verified", HttpStatus.CONFLICT);
        }

        boolean isEmailOtpInvalid = !(userOtp.getEmailOtp().equals(request.getEmailOtp()) || request.getEmailOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));
        boolean isMobileOtpInvalid = !(userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));

        LocalDateTime expiryTime = userOtp.getUpdatedOn().plusMinutes(OTP_EXPIRY_MINUTES);
        if (expiryTime.isBefore(LocalDateTime.now()) || userOtp.getAvailable().equals(RegisterEnum.N)) {
            userOtp.setAvailable(RegisterEnum.N);
            userOtpRepository.save(userOtp);
            throw new CustomException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if(userOtp.getRetryCount() >= OTP_RETRY_COUNT){
            throw new CustomException("OTP retry limit exceeded. Please generate new otp", HttpStatus.BAD_REQUEST);
        }

        if(isEmailOtpInvalid && isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Both Email and Mobile OTPs are invalid", HttpStatus.BAD_REQUEST);
        }
        if (isEmailOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Invalid Email OTP", HttpStatus.BAD_REQUEST);
        }
        if (isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException("Invalid Mobile OTP", HttpStatus.BAD_REQUEST);
        }

        userOtp.setAvailable(RegisterEnum.N);
        userOtp.setRegisterStatus(RegisterEnum.Y);
        userOtp.setOtpCount(0);
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user =userRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));

        user.getPassword().setPassword(encodedPassword);
        userRepository.save(user);

        return new ApiResponse<>(
                true,
                "Password Reset success",
                null,
                LocalDateTime.now(),
                200
        );
    }

//    private String buildEmailTemplate(
//            String title,
//            String body) {
//
//        return
//
//                "<div style=\"font-family:Arial,sans-serif;max-width:500px;margin:0 auto;padding:25px;background:#ffffff;border:1px solid #e0e0e0;border-radius:8px;\">" +
//                        "<div style=\"display:flex;align-items:center;justify-content:center;margin-bottom:20px;\">" +
//                        "<img src=\"cid:logo\" style=\"width:50px;height:50px;margin-right:12px;\">" +
//                        "<h2 style=\"margin:0;color:#1a73e8;font-size:24px;\">" + "Optipace Technologies" + "</h2>" +
//                        "</div>" +
//                        "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin-bottom:25px;\">" +
//                        body
//                        +
//                        "<hr style=\"border:none;border-top:1px solid #e0e0e0;margin-top:30px;margin-bottom:15px;\">" +
//                        "<p style=\"font-size:12px;color:#999;text-align:center;\">" +
//                        "This is an automated operational system email.<br>" +
//                        "Please do not reply directly to this message." +
//                        "</p>" +
//                        "</div>";
//    }

//    private String buildOtpTemplate(String otpCode) {
//
//        String body =
//                "<p style=\"font-size:16px;color:#333;\">Hello,</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.6;\">" +
//                        "Use the verification code below to complete your registration session. This One-Time Password (OTP) is confidential." +
//                        "</p>" +
//                        "<div style=\"text-align:center;margin:35px 0;\">" +
//                        "<span style=\"display:inline-block;font-size:34px;font-weight:bold;color:#1a73e8;letter-spacing:8px;padding:14px 32px;background:#f5f8ff;border:2px dashed #1a73e8;border-radius:8px;\">" +
//                        otpCode
//                        + "</span>" +
//                        "</div>" +
//                        "<p style=\"font-size:14px;color:#666;font-style:italic;text-align:center;\">" +
//                        "Note: This code is valid for <strong>5 minutes</strong> only." +
//                        "</p>";

//        return buildEmailTemplate(
//                "OTP Verification",
//                body);
//    }

//    private String buildAccountCreatedTemplate(String email, String registrationUrl) {
//
//        String body =
//                "<div style=\"text-align:center;margin-bottom:20px;\">" +
//                        "<img src=\"cid:account-created\" " +
//                        "style=\"width:120px;height:auto;\">" +
//                        "</div>" +
//                        "<h1 style=\"margin-top:10px;margin-bottom:20px;color:#1a73e8;text-align:center;font-size:30px;\">" +
//                        "Your Account is Created!" +
//                        "</h1>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.6;\">" +
//                        "Hello," +
//                        "</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.8;\">" +
//                        "Congratulations! Your employee account has been successfully created." +
//                        "</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.8;\">" +
//                        "Your registered email address is:" +
//                        "</p>" +
//                        "<div style=\"margin:25px 0;padding:15px;background:#f5f8ff;border:1px solid #d9e6ff;border-radius:8px;text-align:center;\">" +
//                        "<span style=\"color:#1a73e8;font-size:18px;font-weight:bold;\">" +
//                        email
//                        + "</span>" +
//                        "</div>" +
//                        "<p style=\"text-align:center;color:#555;font-size:15px;line-height:1.7;\">" +
//                        "Please complete your registration to activate your account and access the employee portal." +
//                        "</p>" +
//                        "<div style=\"text-align:center;margin:35px 0;\">" +
//                        "<a href=\"" + registrationUrl + "\" " +
//                        "style=\"background:#1a73e8;color:#ffffff;text-decoration:none;padding:15px 35px;border-radius:6px;display:inline-block;font-size:16px;font-weight:bold;\">" +
//                        "Complete Your Registration" +
//                        "</a>" +
//                        "</div>";
//
//        return buildEmailTemplate("Account Created", body);
//    }

//    private String buildRegistrationCompletedTemplate(String loginUrl) {
//
//        String body =
//
//                "<div style=\"text-align:center;margin-bottom:20px;\">" +
//                        "<img src=\"cid:registration-completed\" " +
//                        "style=\"width:120px;height:auto;\">" +
//                        "</div>" +
//                        "<h1 style=\"margin-top:10px;margin-bottom:20px;color:#28a745;text-align:center;font-size:30px;\">" +
//                        "Your Registration is Completed!" +
//                        "</h1>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.6;\">" +
//                        "Hello," +
//                        "</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.8;\">" +
//                        "Congratulations! Your employee registration has been completed successfully." +
//                        "</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.8;\">" +
//                        "Your account is now active and ready to use." +
//                        "</p>" +
//                        "<p style=\"font-size:16px;color:#333;line-height:1.8;\">" +
//                        "Click the button below to login and start using the employee portal." +
//                        "</p>" +
//                        "<div style=\"text-align:center;margin:35px 0;\">" +
//                        "<a href=\"" + loginUrl + "\" " +
//                        "style=\"background:#28a745;color:#ffffff;text-decoration:none;padding:15px 35px;border-radius:6px;display:inline-block;font-size:16px;font-weight:bold;\">" +
//                        "Login to Your Account" +
//                        "</a>" +
//                        "</div>";
//
//        return buildEmailTemplate("Registration Completed", body);
//    }

    //    private String buildOtpTemplate(String otpCode){
//        return  "    <div style=\"font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
//                "   <div style=\"text-align: center; margin-bottom: 20px;\">" +
//                "   <h2 style=\"color: #1a73e8; margin: 0;\">Optipace Technologies</h2>"+
//                "   </div>" +
//                "   <hr style=\"border: none; border-top: 1px solid #e0e0e0; margin-bottom: 20px;\">"+
//                "   <p style=\"font-size: 16px; color: #333333; line-height: 1.5;\">Hello,</p>"+
//                "   <p style=\"font-size: 16px; color: #333333; line-height: 1.5;\">Use the verification code below to complete your registration session. This One-Time Password (OTP) is confidential.</p>"+
//                "   <div style=\"text-align: center; margin: 30px 0;\">"+
//                "   <span style=\"display: inline-block; font-size: 32px; font-weight: bold; color: #1a73e8; letter-spacing: 5px; padding: 10px 25px; background-color: #f1f3f4; border-radius: 4px; border: 1px dashed #1a73e8;\">" + otpCode + "</span>"+
//                "   </div>"+
//                "   <p style=\"font-size: 14px; color: #666666; font-style: italic; text-align: center;\">Note: This code is valid for 5 minutes only.</p>"+
//                "   <hr style=\"border: none; border-top: 1px solid #e0e0e0; margin-top: 30px; margin-bottom: 15px;\">"+
//                "   <p style=\"font-size: 12px; color: #999999; text-align: center; margin: 0;\">This is an automated operational system email. Please do not reply directly to this message.</p>"+
//                "   </div>";
//    }
}
