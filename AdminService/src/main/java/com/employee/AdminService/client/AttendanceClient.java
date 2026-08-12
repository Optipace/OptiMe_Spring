package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.DateWiseAttendanceRequest;
import com.employee.AdminService.dto.request.EmployeeAttendanceHistoryInternalResponse;
import com.employee.AdminService.dto.request.UpdateCheckOutRecordsRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.EmployeeAttendanceHistoryResponse;
import com.employee.AdminService.dto.response.EmployeeAttendanceResponse;
import com.employee.AdminService.dto.response.WeeklyAttendanceLogsOfEmployeeRes;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {
    @GetMapping("/api/attendance/internal/getTodayAttendanceRecords")
    ApiResponse<List<EmployeeAttendanceResponse>> getTodayAttendanceRecords();

    @PostMapping("/api/attendance/internal/getDateWiseAttendanceRecords")
    ApiResponse<List<EmployeeAttendanceHistoryInternalResponse>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request);

    @GetMapping("/api/attendance/internal/getWeeklyAttendanceLogs")
    ApiResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(@RequestParam String employeeId);

    @PutMapping("/api/attendance/internal/updateCheckOut")
    ApiResponse<?> updateCheckoutRecordByEmployeeId(@RequestBody UpdateCheckOutRecordsRequest request);
}
