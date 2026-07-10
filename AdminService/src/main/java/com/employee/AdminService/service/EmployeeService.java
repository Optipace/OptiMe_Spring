package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.NotificationPayload;
import com.employee.AdminService.dto.request.NotificationRequest;
import com.employee.AdminService.dto.response.ApiResponse;

public interface EmployeeService {
    public ApiResponse<?> getAllAppliedLeaves();

    public ApiResponse<?> sendBroadcastMessage(NotificationRequest request);
}
