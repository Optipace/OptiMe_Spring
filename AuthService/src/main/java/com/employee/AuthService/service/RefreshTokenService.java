package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.RefreshTokenRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.model.User;

public interface RefreshTokenService {
    public String create(User user, Long employeeId );

    public SingleResponse<?> getNewAccessToken(RefreshTokenRequest request);
}
