package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.LoginResponse;
import com.employee.AuthService.dto.response.ValidationResponse;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    public ApiResponse<?> generateOtp(OtpRequest request);

    public ApiResponse<ValidationResponse> validateOtp(ValidationRequest request);

    public ApiResponse<?> completeRegistration(CompleteRegisterRequest request);

    public ApiResponse<LoginResponse> login(LoginRequest request);

    public ApiResponse<?> getMasterDetails();

    public ApiResponse<?> resetPassword(ResetPasswordRequest request);
}
