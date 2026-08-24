package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.request.UpdateIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.NewUserResponse;
import com.employee.AuthService.dto.response.SingleResponse;

public interface InternalService {
    public SingleResponse<NewUserResponse> createIdentity(AuthIdentityRequest request);

    public ApiResponse<?> deleteIdentity(String employeeId);

    public SingleResponse<?> updateIdentity(UpdateIdentityRequest request);
}
