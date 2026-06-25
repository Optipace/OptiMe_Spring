package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.response.ApiResponse;

public interface AttendanceInternalService {
    public ApiResponse<?> getAttendanceStatus(String employeeId);
}
