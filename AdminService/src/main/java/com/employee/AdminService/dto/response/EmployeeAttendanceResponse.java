package com.employee.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceResponse {
    private Long employeeId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String latitude;
    private String longitude;
    private String attendanceStatus;
    private Long attendanceTypeId;
    private Long workMin;
}
