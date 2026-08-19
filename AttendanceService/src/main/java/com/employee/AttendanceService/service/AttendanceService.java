package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.AddEmpAttendanceRequest;
import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.request.EmployeeAttendanceRequest;
import com.employee.AttendanceService.dto.response.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttendanceService {

    public SingleResponse<?> employeeCheckIn(String request, MultipartFile file, String latitude, String longitude, Long attendanceTypeId);

    public SingleResponse<?> employeeCheckOut(String request);

    public SingleResponse<WorkingDetailsResponse> getWorkingDetails(String employeeId);

    public SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(String employeeId);

    public SingleResponse<?> getTodayAttendanceRecords();

    public SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request);

    SingleResponse<?> createEmployeeAttendance(EmployeeAttendanceRequest request, String role);

    SingleResponse<?> addEmployeeAttendance(AddEmpAttendanceRequest request);
}
