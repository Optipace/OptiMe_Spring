package com.employee.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceResponse {
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private Long workMin;
    private Long attendanceTypeId;
    private String status;
}
