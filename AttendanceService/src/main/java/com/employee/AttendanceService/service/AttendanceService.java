package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface AttendanceService {

    public ApiResponse<?> employeeCheckIn(String request, MultipartFile file, String latitude, String longitude, Long attendanceTypeId);

    public ApiResponse<?> employeeCheckOut(String request);

    public ApiResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId);

    public ApiResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(String employeeId);
}
