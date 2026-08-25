package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.DateWiseAttendanceRequest;
import com.employee.AdminService.dto.request.EmployeeAttendanceHistoryInternalResponse;
import com.employee.AdminService.dto.request.UpdateCheckOutRecordsRequest;
import com.employee.AdminService.dto.response.*;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {
    @GetMapping("/api/attendance/internal/getTodayAttendanceRecords")
    SingleResponse<List<EmployeeAttendanceResponse>> getTodayAttendanceRecords();

    @PostMapping("/api/attendance/internal/getDateWiseAttendanceRecords")
    SingleResponse<List<EmployeeAttendanceHistoryInternalResponse>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request);

    @GetMapping("/api/attendance/internal/getWeeklyAttendanceLogs")
    SingleResponse<WeeklyAttendanceLogsOfEmployeeRes> getWeeklyAttendanceLogs(@RequestParam Long employeeId);

    @PutMapping("/api/attendance/internal/updateCheckOut")
    ApiResponse<?> updateCheckoutRecordByEmployeeId(@RequestBody UpdateCheckOutRecordsRequest request);
}
