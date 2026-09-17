package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.request.UpdateIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.NewUserResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.dto.response.UserStatusGatewayResponse;
import org.springframework.http.ResponseEntity;

public interface InternalService {
    public SingleResponse<NewUserResponse> createIdentity(AuthIdentityRequest request);

    public ApiResponse<?> deleteIdentity(String employeeId);

    public SingleResponse<?> updateIdentity(UpdateIdentityRequest request);

    UserStatusGatewayResponse getUserStatus(Long id);
}
