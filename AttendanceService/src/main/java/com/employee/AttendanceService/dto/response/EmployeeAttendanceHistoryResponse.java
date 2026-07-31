package com.employee.AttendanceService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeAttendanceHistoryResponse {
    private String employeeId;
    private String checkInTime;
    private String checkOutTime;
    private String attendanceStatus;
    private Long totalWorkMin;
}
