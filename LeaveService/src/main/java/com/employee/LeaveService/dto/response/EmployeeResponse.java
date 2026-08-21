package com.employee.LeaveService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeName;
    private String employeeId;
    private String emailId;
    private String employeeDesignation;
    private String role;
    private String employeeStatus;
    private boolean canApproveLeave;
}
