package com.employee.AuthService.service.impl;

import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.*;
import com.employee.AuthService.enums.*;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.*;
import com.employee.AuthService.repository.*;
import com.employee.AuthService.service.*;
import com.employee.AuthService.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
//    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserOtpRepository userOtpRepository;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;
//    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public ApiResponse<?> getOtpByIdentifier(String identifier){

        boolean isAlreadyUser = userRepository.findByEmailId(identifier).isPresent()
                || userRepository.findByContact(identifier).isPresent();

        if (isAlreadyUser) {
            throw new CustomException("User already registered with this email or contact number.", HttpStatus.CONFLICT);
        }

        UserOtp userOtp = userOtpRepository.findByIdentifier(identifier)
                .orElseGet(() -> {
                    UserOtp newOtp = new UserOtp();
                    newOtp.setIdentifier(identifier);
                    return newOtp;
                });


        userOtp.setRegisterStatus(RegisterEnum.N);

        userOtp.setOtp(String.valueOf(new Random().nextInt(899999)+100000));
        userOtp.setExpiryTime(LocalDateTime.now().plusMinutes(5));
        userOtpRepository.save(userOtp);

        String otp = userOtp.getOtp();
        return new ApiResponse<>(
                true,
                "Your OTP is valid for 5 minutes only",
                otp,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ApiResponse<?> validateOtp(String identifier, String otp){
        UserOtp userOtp = userOtpRepository.findByIdentifier(identifier)
                .orElseThrow(() -> new CustomException("Otp not found for this identifier", HttpStatus.NOT_FOUND));

        if(userOtp.getRegisterStatus() == RegisterEnum.Y){
            throw new CustomException("User already registered", HttpStatus.CONFLICT);
        }

        LocalDateTime expiryTime = userOtp.getExpiryTime();
        if(expiryTime.isBefore(LocalDateTime.now())){
            throw new CustomException("OTP expired", HttpStatus.BAD_REQUEST);
        }

        if(userOtp.getOtp().equals(otp)){
            userOtp.setRegisterStatus(RegisterEnum.Y);
            userOtpRepository.save(userOtp);
        }else{
            throw new CustomException("Invalid OTP", HttpStatus.BAD_REQUEST);
        }

        return new ApiResponse<>(
                true,
                "OTP validated successfully",
                null,
                LocalDateTime.now(),
                HttpStatus.OK
        );
    }


    @Override
    @Transactional
    public ApiResponse<?> registerUser(RegisterRequest request) {

        UserOtp userOtp = userOtpRepository.findByIdentifier(request.getOtpIdentifier())
                        .orElseThrow(() -> new CustomException("Please request and verify an OTP first.",HttpStatus.BAD_REQUEST));

        if(userOtp.getRegisterStatus() != RegisterEnum.Y){
            throw new CustomException("OTP has not been validated for this user", HttpStatus.BAD_REQUEST);
        }

        userRepository.findByEmailId(request.getEmailId())
                        .ifPresent(u -> {throw new CustomException("Email already registered!", HttpStatus.CONFLICT);});

        userRepository.findByEmployeeId(request.getEmployeeId())
                .ifPresent(u -> {throw new CustomException("Employee ID must be unique", HttpStatus.CONFLICT);});

        userRepository.findByContact(request.getContact())
                .ifPresent(u -> {throw new CustomException("Contact number already taken", HttpStatus.CONFLICT);});

        User user = new User();
        user.setUserName(request.getUserName());
//        user.setUserStatus(UserStatusEnum.PRESENT);
        user.setRegisterStatus(RegisterEnum.Y);
        user.setEmployeeId(request.getEmployeeId());
        user.setEmailId(request.getEmailId());
        user.setContact(request.getContact());
        user.setCreatedOn(LocalDateTime.now());

        Password password = new Password();
        password.setPassword(passwordEncoder.encode(request.getPassword()));
        password.setUser(user);

        user.setPassword(password);

        if (request.getRole() == RoleEnum.ADMIN) {
            user.setRole(RoleEnum.ADMIN);
        } else {
            user.setRole(RoleEnum.EMP);
        }

        userRepository.save(user);

//        Employee newEmployee = new Employee();
//        newEmployee.setEmployeeId(user.getEmployeeId());
//        newEmployee.setEmployeeName(user.getUserName());
//        newEmployee.setContact(user.getContact());
//        newEmployee.setEmailId(user.getEmailId());
//        newEmployee.setDesignation(EmployeeDesignationEnum.valueOf(request.getDesignation().toUpperCase()));
//        newEmployee.setGender(request.getGender());
//        newEmployee.setAddress(request.getAddress());
//        newEmployee.setUser(user);
//
//        employeeRepository.save(newEmployee);

        userOtpRepository.delete(userOtp);

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


        String accessToken = jwtUtil.generateToken(user.getUserName(), user.getContact(), user.getEmailId(), user.getEmployeeId());
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
}
