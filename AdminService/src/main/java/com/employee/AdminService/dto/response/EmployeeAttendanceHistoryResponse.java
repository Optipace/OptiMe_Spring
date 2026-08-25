package com.employee.AdminService.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class EmployeeAttendanceHistoryResponse {
    private Long id;

    private Long employeeId;

    private LocalDateTime checkInTime;

    private LocalDateTime checkOutTime;

    private String attendanceStatus;

    private Long totalWorkMin;

    private Long attendanceTypeId;
}
