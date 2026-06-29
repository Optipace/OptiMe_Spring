package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.LoginRequest;
import com.employee.AuthService.dto.request.OtpRequest;
import com.employee.AuthService.dto.request.CompleteRegisterRequest;
import com.employee.AuthService.dto.request.ValidationRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.LoginResponse;
import com.employee.AuthService.dto.response.ValidationResponse;

public interface UserService {

    public ApiResponse<?> generateOtp(OtpRequest request);

    public ApiResponse<ValidationResponse> validateOtp(ValidationRequest request);

    public ApiResponse<?> completeRegistration(CompleteRegisterRequest request);

    public ApiResponse<LoginResponse> login(LoginRequest request);

    public ApiResponse<?> getMasterDetails();
}
