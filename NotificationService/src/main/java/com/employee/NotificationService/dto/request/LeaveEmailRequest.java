package com.employee.NotificationService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveEmailRequest {
    private String authorityEmailId;
    private String employeeId;
    private String employeeName;
    private String employeeEmailId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
    private String leaveType;
}
