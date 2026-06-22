package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.AuthIdentityRequest;
import com.employee.AuthService.dto.response.ApiResponse;

public interface InternalService {
    public ApiResponse<?> createIdentity(AuthIdentityRequest request);
}
