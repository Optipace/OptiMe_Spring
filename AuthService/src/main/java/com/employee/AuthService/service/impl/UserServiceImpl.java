package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.AdminClient;
import com.employee.AuthService.client.CommunicationClient;
import com.employee.AuthService.client.EmployeeClient;
import com.employee.AuthService.client.LeaveClient;
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
import java.util.ArrayList;
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
    private final LeaveClient leaveClient;

    @Override
    @Transactional
    public SingleResponse<?> generateOtp(OtpRequest request) {

        boolean isAlreadyUser = userRepository.findByEmailId(request.getEmailId()).isPresent()
                && userRepository.findByContact(request.getContact()).isPresent();

        if (!isAlreadyUser) {
            throw new CustomException(null, CustomStatus.NOT_REGISTERED, 409);
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
                    throw new CustomException("Try again in " + minutes + " min " + seconds + " sec",
                            CustomStatus.OTP_RETRY_LIMIT_EXCEEDED, 409);
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
            throw new CustomException(null, CustomStatus.EMAIL_SENDING_FAILED, 409);
        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<ValidationResponse> validateOtp(ValidationRequest request) {
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException(null, CustomStatus.OTP_NOT_FOUND, 409));

        // Check otp is already registered or not
        if (userOtp.getRegisterStatus() == RegisterEnum.Y) {
            throw new CustomException(null, CustomStatus.OTP_ALREADY_VERIFIED, 409);
        }

        boolean isEmailOtpInvalid = !(userOtp.getEmailOtp().equals(request.getEmailOtp()) || request.getEmailOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));
        boolean isMobileOtpInvalid = !(userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));

        LocalDateTime expiryTime = userOtp.getUpdatedOn().plusMinutes(OTP_EXPIRY_MINUTES);
        if (expiryTime.isBefore(LocalDateTime.now()) || userOtp.getAvailable().equals(RegisterEnum.N)) {
            userOtp.setAvailable(RegisterEnum.N);
            userOtpRepository.save(userOtp);
            throw new CustomException(null, CustomStatus.OTP_EXPIRED, 409);
        }

        if(userOtp.getRetryCount() >= OTP_RETRY_COUNT){
            throw new CustomException(null, CustomStatus.OTP_RETRY_LIMIT_EXCEEDED, 409);
        }

        if(isEmailOtpInvalid && isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_OTP, 409);
        }
        if (isEmailOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_EMAIL_OTP, 409);
        }
        if (isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_MOBILE_OTP, 409);
        }

        userOtp.setAvailable(RegisterEnum.N);       // Expire the otp
        userOtp.setRegisterStatus(RegisterEnum.Y);  // Employee OTP registered successfully
        userOtp.setValidated(StatusEnum.F); // Set validation token to False
        userOtp.setOtpCount(0);
        userOtp.setValidationToken(UUID.randomUUID().toString()); // Random validation token generation
        userOtpRepository.saveAndFlush(userOtp);

        User user = userRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 409));

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
        return new SingleResponse<>(
                validationResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    @Transactional
    public SingleResponse<?> completeRegistration(CompleteRegisterRequest request) {

        User user = userRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_ID_NOT_FOUND, 409));

        UserOtp userOtp = userOtpRepository.findByEmailIdOrContact(user.getEmailId(), user.getContact())
                .orElseThrow(() -> new CustomException(null, CustomStatus.IDENTITY_NOT_FOUND, 409));

        if (!userOtp.getValidationToken().equals(request.getValidationToken())) {
            throw new CustomException(null, CustomStatus.INVALID_VALIDATION_TOKEN, 409);
        }
//        userRepository.findByEmployeeId(request.getEmployeeId())
//                .ifPresent(u -> {throw new CustomException("Employee ID must be unique", HttpStatus.CONFLICT);});

        if (userOtp.getRegisterStatus() != RegisterEnum.Y) {
            throw new CustomException(null, CustomStatus.OTP_NOT_VALIDATED, 409);
        }

        // Prevent Duplicate Registration (Fixes the User ID already exists crash)
        boolean isAlreadyRegistered = passwordRepository.existsByUserId(user.getId());
        if (isAlreadyRegistered) {
            throw new CustomException(null, CustomStatus.EMPLOYEE_ID_ALREADY_REGISTERED, 409);
        }

        // Check if personal email is already claimed by someone else
        boolean isEmailTaken = userRepository.existsByPersonalEmailAndEmployeeIdNot(
                request.getPersonalEmail(), request.getEmployeeId());
        if (isEmailTaken) {
            throw new CustomException(null, CustomStatus.PERSONAL_EMAIL_ALREADY_EXISTS, 409);
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

            return new SingleResponse<>(
                    null,
                    CustomStatus.EMAIL_SENDING_FAILED
            );
        }

//        try {
//            emailService.sendHtmlEmail(user.getEmailId(), subject, htmlBody);
//        } catch (Exception e) {
//            log.error("Email sending failed", e);
//        }
        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    public SingleResponse<LoginResponse> login(LoginRequest request) {

        User user = userRepository.findByEmailIdOrContact(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new CustomException(null, CustomStatus.EMPLOYEE_NOT_FOUND, 409));

        if (user.getPassword() == null) {
            throw new CustomException(null, CustomStatus.INVALID_PASSWORD, 409);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword().getPassword()) || request.getPassword() == null || user.getPassword() == null) {
            throw new CustomException(null, CustomStatus.INVALID_PASSWORD, 409);
        }

        String accessToken = jwtUtil.generateToken(user.getUserName(), user.getContact(), user.getEmailId(), user.getEmployeeId(), String.valueOf(user.getRole()));
        String refreshToken = refreshTokenService.create(user);

        if (user.getUserStatus() == null || user.getUserStatus() == UserStatusEnum.INACTIVE) {
            user.setUserStatus(UserStatusEnum.ACTIVE);
            userRepository.save(user);
        }

        LoginResponse loginResponse = new LoginResponse(accessToken, refreshToken);

        return new SingleResponse<>(
                loginResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<MasterResponse> getMasterDetails() {

        ApiResponse<List<OfficeResponse>> officeResponse = adminClient.getOfficeList();
        ApiResponse<MasterEmployeeResponse> empResponse = employeeClient.getMasterDetails();
        ApiResponse<List<LeaveTypeResponse>> leaveResponse = leaveClient.getLeaveTypeList();

        List<EmployeeDesignationResponse> employeeDesignationResponseList = new ArrayList<>();
        List<RoleEnum> roleEnumList = new ArrayList<>();
        List<WorkTypeResponse> workTypeList = new ArrayList<>();
        List<EmployeeStatusResponse> employeeStatusList = new ArrayList<>();
        if(empResponse != null && empResponse.getData() != null){
            employeeDesignationResponseList = empResponse.getData().getAvailableDesignationsList();
            roleEnumList = empResponse.getData().getRoleEnumList();
            workTypeList = empResponse.getData().getWorkTypeList();
            employeeStatusList = empResponse.getData().getEmployeeStatusList();
        }else{
            throw new CustomException(null, CustomStatus.MICROSERVICE_CALL_FAILED, 500);
        }

//        MasterResponse masterResponse = (empResponse != null && empResponse.getData() != null)
//                ? empResponse.getData()
//                : new MasterResponse();

        MasterResponse masterResponse = new MasterResponse();

        if (officeResponse != null && officeResponse.getData() != null) {
            masterResponse.setOfficeResponse(officeResponse.getData());
        }

        if(leaveResponse != null && leaveResponse.getData() != null){
            masterResponse.setLeaveTypeResponseList(leaveResponse.getData());
        }

        masterResponse.setAvailableDesignations(employeeDesignationResponseList);
        masterResponse.setWorkTypeList(workTypeList);
        masterResponse.setRoleEnumList(roleEnumList);
        masterResponse.setEmployeeStatusList(employeeStatusList);

        return new SingleResponse<>(
                masterResponse,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> resetPassword(ResetPasswordRequest request) {
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseThrow(() -> new CustomException(null, CustomStatus.IDENTITY_NOT_FOUND, 409));

        // Check otp is already registered or not
        if (userOtp.getRegisterStatus() == RegisterEnum.Y) {
            throw new CustomException(null, CustomStatus.OTP_ALREADY_VERIFIED, 409);
        }

        boolean isEmailOtpInvalid = !(userOtp.getEmailOtp().equals(request.getEmailOtp()) || request.getEmailOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));
        boolean isMobileOtpInvalid = !(userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals(String.valueOf(appProperties.getOtp().getFixed())));

        LocalDateTime expiryTime = userOtp.getUpdatedOn().plusMinutes(OTP_EXPIRY_MINUTES);
        if (expiryTime.isBefore(LocalDateTime.now()) || userOtp.getAvailable().equals(RegisterEnum.N)) {
            userOtp.setAvailable(RegisterEnum.N);
            userOtpRepository.save(userOtp);
            throw new CustomException(null, CustomStatus.OTP_EXPIRED, 409);
        }

        if(userOtp.getRetryCount() >= OTP_RETRY_COUNT){
            throw new CustomException(null, CustomStatus.OTP_RETRY_LIMIT_EXCEEDED, 409);
        }

        if(isEmailOtpInvalid && isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_OTP, 409);
        }
        if (isEmailOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_EMAIL_OTP, 409);
        }
        if (isMobileOtpInvalid){
            userOtp.setRetryCount(userOtp.getRetryCount() + 1);
            userOtpRepository.saveAndFlush(userOtp);
            throw new CustomException(null, CustomStatus.INVALID_MOBILE_OTP, 409);
        }

        userOtp.setAvailable(RegisterEnum.N);
        userOtp.setRegisterStatus(RegisterEnum.Y);
        userOtp.setOtpCount(0);
        String rawPassword = request.getPassword();
        String encodedPassword = passwordEncoder.encode(rawPassword);
        User user =userRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseThrow(() -> new CustomException(null, CustomStatus.USER_NOT_FOUND, 409));

        user.getPassword().setPassword(encodedPassword);
        userRepository.save(user);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> updatePassword(UpdatePasswordRequest request, String employeeId) {
        User user = userRepository.findByEmployeeId(employeeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.USER_NOT_FOUND, 409));

        String oldPassword = request.getOldPassword();

        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword().getPassword())){
            throw new CustomException(null, CustomStatus.INVALID_PASSWORD, 409);
        }

        if(oldPassword.equals(request.getNewPassword())){
            throw new CustomException(null, CustomStatus.SAME_AS_OLD_PASSWORD, 409);
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());
        Password password = user.getPassword();
        password.setPassword(encodedPassword);
        user.setPassword(password);
        userRepository.save(user);

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }
}
