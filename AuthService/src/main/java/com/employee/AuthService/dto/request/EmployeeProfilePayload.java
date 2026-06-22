package com.employee.AuthService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeProfilePayload {
    private String employeeId;
    private String employeeName;
    private String address;
    private String dateOfBirth;
    private String emergencyContact;
}
