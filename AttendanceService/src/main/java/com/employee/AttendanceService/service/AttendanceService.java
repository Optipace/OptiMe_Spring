package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.*;
import com.employee.AttendanceService.dto.response.*;

import java.util.List;

public interface AttendanceService {

    public ApiResponse<?> employeeCheckIn(String request);

    public ApiResponse<?> employeeCheckOut(String request);

    public ApiResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId);
}
