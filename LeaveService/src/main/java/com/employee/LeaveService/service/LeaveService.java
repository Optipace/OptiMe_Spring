package com.employee.LeaveService.service;

import com.employee.LeaveService.dto.request.LeaveRequest;
import com.employee.LeaveService.dto.response.ApiResponse;

public interface LeaveService {
    public ApiResponse<?> saveLeaveApplication(LeaveRequest request, String employeeId, String employeeName);
}
