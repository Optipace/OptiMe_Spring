package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.DateWiseAttendanceRequest;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.EmployeeAttendanceHistoryResponse;
import com.employee.AdminService.dto.response.EmployeeAttendanceResponse;
import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {
    @GetMapping("/api/attendance/internal/getTodayAttendanceRecords")
    ApiResponse<List<EmployeeAttendanceResponse>> getTodayAttendanceRecords();

    @PostMapping("/api/attendance/internal/getDateWiseAttendanceRecords")
   ApiResponse<List<EmployeeAttendanceHistoryResponse>> getDateWiseAttendanceRecords(@Valid @RequestBody DateWiseAttendanceRequest request);
}
