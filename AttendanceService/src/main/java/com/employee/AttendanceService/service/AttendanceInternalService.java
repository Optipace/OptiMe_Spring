package com.employee.AttendanceService.service;

import com.employee.AttendanceService.dto.request.DateWiseAttendanceRequest;
import com.employee.AttendanceService.dto.request.UpdateCheckOutRecordsRequest;
import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.EmployeeAttendanceHistoryResponse;
import com.employee.AttendanceService.dto.response.SingleResponse;
import com.employee.AttendanceService.dto.response.WeeklyAttendanceLogsOfEmployeeRes;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface AttendanceInternalService {
    public SingleResponse<?> getAttendanceStatus(Long employeeId);

    public SingleResponse<?> getTodayAttendanceRecords();

    public SingleResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(DateWiseAttendanceRequest request);

    public SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(Long employeeId);

    ApiResponse<?> updateCheckoutRecordsByEmpId(UpdateCheckOutRecordsRequest request);
}
