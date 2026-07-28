package com.employee.AdminService.client;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.EmployeeAttendanceResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {
    @GetMapping("/api/attendance/internal/getTodayAttendanceRecords")
    ApiResponse<List<EmployeeAttendanceResponse>> getTodayAttendanceRecords();
}
