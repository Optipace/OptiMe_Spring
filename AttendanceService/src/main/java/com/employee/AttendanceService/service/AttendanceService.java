package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.*;
import com.employee.AttendanceService.dto.response.*;

import java.util.List;

public interface AttendanceService {

    public ApiResponse<?> employeeLogin(EmployeeLoginRequest request);

    public ApiResponse<?> employeeLogout(EmployeeLogoutRequest request);

    public ApiResponse<List<AttendanceResponse>> getWorkingDetails(String employeeId);
}
