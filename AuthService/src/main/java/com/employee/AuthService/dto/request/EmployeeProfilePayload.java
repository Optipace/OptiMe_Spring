package com.employee.AuthService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeProfilePayload {
    private String employeeId;
    private String currentAddress;
    private String emergencyContact;
    private String bloodGroup;
}
