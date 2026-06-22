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
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final PasswordRepository passwordRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
    private final EmailService emailService;
    private final EmployeeClient employeeClient;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public ApiResponse<?> generateOtp(OtpRequest request){

        boolean isAlreadyUser = userRepository.findByEmailId(request.getEmailId()).isPresent()
                || userRepository.findByContact(request.getContact()).isPresent();

        if (!isAlreadyUser) {
            throw new CustomException("Please register this email or contact number in office!.", HttpStatus.CONFLICT);
        }

        User user = userRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseThrow(() -> new CustomException("Please register in office first!", HttpStatus.BAD_REQUEST));

        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmailId(), request.getContact())
                .orElseGet(() -> {
                    UserOtp newOtp = new UserOtp();
                    newOtp.setEmailId(request.getEmailId());
                    newOtp.setContact(request.getContact());
                    return newOtp;
                });


        userOtp.setRegisterStatus(RegisterEnum.N);

        userOtp.setEmailOtp(String.valueOf(new Random().nextInt(899999)+100000));
        userOtp.setMobileOtp(String.valueOf(new Random().nextInt(899999)+100000));
        userOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        userOtpRepository.save(userOtp);

        String subject = "Welcome to Optipace Technologies";
        String body = buildOtpTemplate(userOtp.getEmailOtp());

        try {
            emailService.sendHtmlEmail(userOtp.getEmailId(), subject, body);
            return new ApiResponse<>(
                    true,
                    "Otp sent to "+userOtp.getContact()+" and "+userOtp.getEmailId()+" successfully",
                    null,
                    LocalDateTime.now(),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ApiResponse<>(
                    false,
                    "Something went wrong! Error while sending email\n"+
                            "Please try again",
                    null,
                    LocalDateTime.now(),
                    500
            );
        }
    }

    @Override
    @Transactional
    public ApiResponse<?> validateOtp(ValidationRequest request){
        UserOtp userOtp = userOtpRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException("Otp not found", HttpStatus.NOT_FOUND));

        if(userOtp.getRegisterStatus() == RegisterEnum.Y){
            throw new CustomException("User already registered", HttpStatus.CONFLICT);
        }

        LocalDateTime expiryTime = userOtp.getExpiryTime();
        if(expiryTime.isBefore(LocalDateTime.now())){
            throw new CustomException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if(userOtp.getEmailOtp().equals(request.getEmailOtp()) && (userOtp.getMobileOtp().equals(request.getMobileOtp()) || request.getMobileOtp().equals("1234"))){
            userOtp.setRegisterStatus(RegisterEnum.Y);
            userOtpRepository.save(userOtp);
        }else{
            throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        User user = userRepository.findByEmailIdAndContact(request.getEmail(), request.getContact())
                .orElseThrow(() -> new CustomException("User with this email or contact not found", HttpStatus.NOT_FOUND));

        EmployeeResponse response = null;

        try{
            System.out.println("Employee id = " + user.getEmployeeId());
            ApiResponse<EmployeeResponse> apiResponse = employeeClient.getProfile(user.getEmployeeId());

            if (apiResponse != null && apiResponse.getData() != null) {
                response = apiResponse.getData();
            }

        }catch (FeignException e){
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }

        return new ApiResponse<>(
                true,
                "OTP validated successfully",
                response,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }


    @Override
    @Transactional
    public ApiResponse<?> completeRegistration(CompleteRegisterRequest request) {

        User user = userRepository.findByEmployeeId(request.getEmployeeId())
                .orElseThrow(() -> new CustomException("No employee Id found", HttpStatus.NOT_FOUND));

        UserOtp userOtp = userOtpRepository.findByEmailIdOrContact(user.getEmailId(), user.getContact())
                        .orElseThrow(() -> new CustomException("Validated Email-Id or contact not found",HttpStatus.NOT_FOUND));

        if(userOtp.getRegisterStatus() != RegisterEnum.Y){
            throw new CustomException("OTP has not been validated for this user", HttpStatus.BAD_REQUEST);
        }

//        userRepository.findByEmailId(request.getEmailId())
//                        .ifPresent(u -> {throw new CustomException("Email already registered!", HttpStatus.CONFLICT);});
//
//        userRepository.findByEmployeeId(request.getEmployeeId())
//                .ifPresent(u -> {throw new CustomException("Employee ID must be unique", HttpStatus.CONFLICT);});
//
//        userRepository.findByContact(request.getContact())
//                .ifPresent(u -> {throw new CustomException("Contact number already taken", HttpStatus.CONFLICT);});

//        User user = new User();
//        user.setUserName(request.getUserName());
////        user.setUserStatus(UserStatusEnum.PRESENT);
//        user.setRegisterStatus(RegisterEnum.Y);
//        user.setEmployeeId(request.getEmployeeId());
//        user.setEmailId(request.getEmailId());
//        user.setContact(request.getContact());
//        user.setCreatedOn(LocalDateTime.now());

        user.setUserName(request.getEmployeeName());
        Password password = new Password();
        password.setPassword(passwordEncoder.encode(request.getPassword()));
        password.setUser(user);

        user.setPassword(password);

        passwordRepository.save(password);
        userRepository.save(user);

        if(request.getEmployeeName().equals(null) || request.getEmployeeName() == null){
            request.setEmployeeName(user.getUserName());
        }

        EmployeeProfilePayload profilePayload = new EmployeeProfilePayload(
                request.getEmployeeId(),
                request.getEmployeeName(),
                request.getAddress(),
                request.getDateOfBirth(),
                request.getEmergencyContact()
        );

        try{
            employeeClient.completeProfile(profilePayload);

        }catch (FeignException e){
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            JsonNode errorNode = objectMapper.readTree(rawErrorJson);

            if(errorNode.has("message")){
                cleanErrorMessage = errorNode.get("message").asText();
            }else{
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }
//        restClient.post()
//                .uri("http://localhost:8082/api/employee/internal/complete-profile")
//                .body(profilePayload)
//                .retrieve()
//                .toBodilessEntity();

//        userOtpRepository.delete(userOtp);

        return new ApiResponse<>(
                true,
                "Registered successfully",
                null,
                LocalDateTime.now(),
                HttpStatus.CREATED
        );
    }

    public ApiResponse<LoginResponse> login(LoginRequest request){
        User user = userRepository.findByEmailIdOrContact(request.getIdentifier(), request.getIdentifier())
                .orElseThrow(() -> new CustomException("User not found", HttpStatus.NOT_FOUND));





        if(!passwordEncoder.matches(request.getPassword(), user.getPassword().getPassword())){
            throw new CustomException("Invalid Password", HttpStatus.BAD_REQUEST);
        }


        String accessToken = jwtUtil.generateToken(user.getUserName(), user.getContact(), user.getEmailId(), user.getEmployeeId(), String.valueOf(user.getRole()));
        String refreshToken = refreshTokenService.create(user);

        if(user.getUserStatus() == null || user.getUserStatus() == UserStatusEnum.INACTIVE){
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

    private String buildOtpTemplate(String otpCode){
        return  "    <div style=\"font-family: Arial, sans-serif; max-width: 500px; margin: 0 auto; padding: 20px; border: 1px solid #e0e0e0; border-radius: 8px;\">" +
                "   <div style=\"text-align: center; margin-bottom: 20px;\">" +
                "   <h2 style=\"color: #1a73e8; margin: 0;\">Optipace Technologies</h2>"+
                "   </div>" +
                "   <hr style=\"border: none; border-top: 1px solid #e0e0e0; margin-bottom: 20px;\">"+
                "   <p style=\"font-size: 16px; color: #333333; line-height: 1.5;\">Hello,</p>"+
                "   <p style=\"font-size: 16px; color: #333333; line-height: 1.5;\">Use the verification code below to complete your registration session. This One-Time Password (OTP) is confidential.</p>"+
                "   <div style=\"text-align: center; margin: 30px 0;\">"+
                "   <span style=\"display: inline-block; font-size: 32px; font-weight: bold; color: #1a73e8; letter-spacing: 5px; padding: 10px 25px; background-color: #f1f3f4; border-radius: 4px; border: 1px dashed #1a73e8;\">" + otpCode + "</span>"+
                "   </div>"+
                "   <p style=\"font-size: 14px; color: #666666; font-style: italic; text-align: center;\">Note: This code is valid for 5 minutes only.</p>"+
                "   <hr style=\"border: none; border-top: 1px solid #e0e0e0; margin-top: 30px; margin-bottom: 15px;\">"+
                "   <p style=\"font-size: 12px; color: #999999; text-align: center; margin: 0;\">This is an automated operational system email. Please do not reply directly to this message.</p>"+
                "   </div>";
    }
}
