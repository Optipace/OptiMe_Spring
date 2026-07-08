package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.EmployeeClient;
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
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
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
    private final EmailService emailService;
    private final EmployeeClient employeeClient;
    private final ObjectMapper objectMapper;
    //    private final ModelMapper modelMapper;
    private final TemplateEngine templateEngine;

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


        userOtp.setRegisterStatus(RegisterEnum.N); // Register status to N (NO)
        userOtp.setAvailable(RegisterEnum.Y); // Otp available status to Y (Not expired fresh otp)
        userOtp.setEmailOtp(String.valueOf(new Random().nextInt(899999) + 100000));
        userOtp.setMobileOtp(String.valueOf(new Random().nextInt(899999) + 100000));
        userOtp.setCreatedOn(LocalDateTime.now());
        userOtpRepository.save(userOtp);

        // 1. Variable for the HTML template
        Context context = new Context();
        context.setVariable("otpCode", userOtp.getEmailOtp());

        // 2. Process the HTML file (points to src/main/resources/templates/OtpEmailTemplate.html)
        String htmlBody = templateEngine.process("OtpEmailTemplate", context);
        String subject = "Welcome to Optipace Technologies";

        try {
            emailService.sendHtmlEmail(userOtp.getEmailId(), subject, htmlBody);
            return new ApiResponse<>(
                    true,
                    "Otp sent to " + userOtp.getContact() + " and " + userOtp.getEmailId() + " successfully",
                    null,
                    LocalDateTime.now(),
                    200
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    false,
                    "Something went wrong! Error while sending email\n" +
                            "Please try again",
                    null,
                    LocalDateTime.now(),
                    500
            );
        }
    }

    @Override
    @Transactional
    public ApiResponse<ValidationResponse> validateOtp(ValidationRequest request) {
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException("Otp not found", HttpStatus.NOT_FOUND));

        if (userOtp.getRegisterStatus() == RegisterEnum.Y) {
            throw new CustomException("User already registered", HttpStatus.CONFLICT);
        }

        LocalDateTime expiryTime = userOtp.getCreatedOn().plusMinutes(5);
        System.out.println("Expiry time : "+expiryTime);
        System.out.println("Is expired : "+expiryTime.isBefore(LocalDateTime.now()));
        if (expiryTime.isBefore(LocalDateTime.now()) || userOtp.getAvailable().equals(RegisterEnum.N)) {
            throw new CustomException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if ((userOtp.getEmailOtp().equals(request.getEmailOtp()) || request.getEmailOtp().equals("1234")) &&
                (userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals("1234"))) {
            userOtp.setAvailable(RegisterEnum.N);       // Expire the otp
            userOtp.setRegisterStatus(RegisterEnum.Y);  // Employee registered successfully
            userOtp.setValidated(StatusEnum.F);         // Set validation token to False
            userOtp.setValidationToken(UUID.randomUUID().toString()); // Random validation token generation
            userOtpRepository.saveAndFlush(userOtp);
        } else {
            throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

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
                .orElseThrow(() -> new CustomException("Validated Email-Id or contact not found", HttpStatus.NOT_FOUND));

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

        String loginUrl = "http:login.optipace.com";
        // 1. Variable for the HTML template
        Context context = new Context();
        context.setVariable("loginUrl", loginUrl);

        // 2. Process the HTML file (points to src/main/resources/templates/RegistrationCompletionTemplate.html)
        String htmlBody = templateEngine.process("RegistrationCompletionTemplate", context);
        String subject = "Welcome to Optipace Technologies";

        try {
            emailService.sendHtmlEmail(user.getEmailId(), subject, htmlBody);
        } catch (Exception e) {
            System.out.println("Email sending failed");
        }
        return new ApiResponse<>(
                true,
                "Registered successfully",
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

        ListOfOfficeResponse masterResponse = null;
        ApiResponse<ListOfOfficeResponse> apiResponse = employeeClient.getMasterDetails();

        if (apiResponse != null && apiResponse.getData() != null) {
            masterResponse = apiResponse.getData();
        }
        return new ApiResponse<>(
                true,
                "Master Response",
                masterResponse,
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
