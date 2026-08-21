package com.employee.AdminService.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeAttendanceHistoryInternalResponse {

    private Long id;
    private Long employeeId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String attendanceStatus;
    private Long totalWorkMin;
    private Long attendanceTypeId;
}
