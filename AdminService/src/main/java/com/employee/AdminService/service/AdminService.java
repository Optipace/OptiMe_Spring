package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.NotificationRequest;
import com.employee.AdminService.dto.request.RegisterRequest;
import com.employee.AdminService.dto.response.ApiResponse;

public interface AdminService {
    public ApiResponse<?> addNewUser(RegisterRequest request, String adminEmployeeId);

    public ApiResponse<?> getAllAppliedLeaves();

    public ApiResponse<?> sendBroadcastMessage(NotificationRequest request);
}
