package com.employee.EmployeeProfileService.dto.response;

import com.employee.EmployeeProfileService.enums.EmployeeStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeInternalResponse {
    private String employeeName;
    private String employeeId;
    private String emailId;
    private String employeeDesignation;
    private String role;
    private EmployeeStatusEnum employeeStatus;
}
