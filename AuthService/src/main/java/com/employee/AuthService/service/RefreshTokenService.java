package com.employee.AuthService.service;

import com.employee.AuthService.dto.request.RefreshTokenRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.model.User;

public interface RefreshTokenService {
    public String create(User user);

    public ApiResponse<?> getNewAccessToken(RefreshTokenRequest request);
}
