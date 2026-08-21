package com.employee.AttendanceService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceHistoryResponse {
    private Long id; // TODO : Can be renamed as attendanceId
    private Long employeeId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String attendanceStatus;
    private Long totalWorkMin;
    private Long attendanceTypeId;
}
