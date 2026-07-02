package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.EmployeeStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeStatusRequest {
    private String employeeId;
    private EmployeeStatusEnum employeeStatus;
}
