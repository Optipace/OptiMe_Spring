package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

public interface AttendanceService {

    public SingleResponse<?> employeeCheckIn(String request, MultipartFile file, String latitude, String longitude, Long attendanceTypeId);

    public SingleResponse<?> employeeCheckOut(String request);

    public SingleResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId);

    public SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(String employeeId);
}
