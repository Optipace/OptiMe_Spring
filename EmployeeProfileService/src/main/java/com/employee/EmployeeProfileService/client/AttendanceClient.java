package com.employee.EmployeeProfileService.client;

import com.employee.EmployeeProfileService.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "ATTENDANCE-SERVICE", url = "http://localhost:7074")
@FeignClient(name = "ATTENDANCE-SERVICE")
public interface AttendanceClient {

    @GetMapping("/api/attendance/internal/getAttendanceStatus")
    public ApiResponse<?> getAttendanceStatus(@RequestParam("employeeId") String employeeId);
}
