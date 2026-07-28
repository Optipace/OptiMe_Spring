package com.employee.AttendanceService.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/isEmployeeOnLeave")
    boolean isEmployeeOnLeave(@RequestParam("employeeId")String employeeId,@RequestParam("today") LocalDate today);
}
