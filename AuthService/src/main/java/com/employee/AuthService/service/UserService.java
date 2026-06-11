package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.LoginRequest;
import com.employee.AuthService.dto.request.RegisterRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.LoginResponse;

public interface UserService {

    public ApiResponse<?> getOtpByIdentifier(String identifier);

    public ApiResponse<?> validateOtp(String identifier, String otp);

    public ApiResponse<?> registerUser(RegisterRequest request);

    public ApiResponse<LoginResponse> login(LoginRequest request);
}
