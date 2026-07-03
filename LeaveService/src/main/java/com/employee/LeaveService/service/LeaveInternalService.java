package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.response.ApiResponse;

public interface LeaveInternalService {
    ApiResponse<?> getAllAppliedLeaves();
}
