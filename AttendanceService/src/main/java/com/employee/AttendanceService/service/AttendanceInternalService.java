package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.EmployeeAttendanceHistoryResponse;

import java.util.List;

public interface AttendanceInternalService {
    public ApiResponse<?> getAttendanceStatus(String employeeId);

    public ApiResponse<?> getTodayAttendanceRecords();

    public ApiResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request);
}
