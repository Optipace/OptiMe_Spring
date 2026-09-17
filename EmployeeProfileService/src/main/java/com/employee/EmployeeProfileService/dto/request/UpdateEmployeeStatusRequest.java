package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.AccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeStatusRequest {
    private Long employeeId;
    private AccountStatus accountStatus;
}