package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.*;
import com.employee.AuthService.dto.response.*;
import org.springframework.web.bind.annotation.RequestBody;

public interface UserService {

    public SingleResponse<?> generateOtp(OtpRequest request);

    public SingleResponse<ValidationResponse> validateOtp(ValidationRequest request);

    public SingleResponse<?> completeRegistration(CompleteRegisterRequest request);

    public SingleResponse<LoginResponse> login(LoginRequest request);

    public SingleResponse<MasterResponse> getMasterDetails();

    public SingleResponse<?> resetPassword(ResetPasswordRequest request);

    public SingleResponse<?> updatePassword(UpdatePasswordRequest request, String employeeId);

    SingleResponse<String> updateUserStatus(Long userId);

    SingleResponse<String> updateUserIsDiscontinued(Long userId);
}
