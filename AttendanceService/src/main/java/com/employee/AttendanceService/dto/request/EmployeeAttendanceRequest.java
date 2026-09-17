package com.employee.AttendanceService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Null;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class EmployeeAttendanceRequest {
    private Long id;
    private Long employeeId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Null
    private LocalDateTime checkOutTime;
    private Long attendanceTypeId;
    private String remarks;
}
