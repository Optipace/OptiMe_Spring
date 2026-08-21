package com.employee.AttendanceService.client;

import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.SingleResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Set;

@FeignClient(name = "LEAVE-SERVICE")
public interface LeaveClient {
    @GetMapping("/api/leave/internal/isEmployeeOnLeave")
    boolean isEmployeeOnLeave(@RequestParam("employeeId")Long employeeId,@RequestParam("today") LocalDate today);

    @GetMapping("/api/leave/internal/leaveDates/{employeeId}")
    SingleResponse<Set<LocalDate>> getEmployeeLeaveDatesInRange(
            @PathVariable("employeeId") Long employeeId,
            @RequestParam("startDate") LocalDate startDate,
            @RequestParam("endDate") LocalDate endDate
    );
}
