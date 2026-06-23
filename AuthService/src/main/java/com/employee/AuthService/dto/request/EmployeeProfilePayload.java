package com.employee.AuthService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeProfilePayload {
    private String employeeId;
    private String employeeName;
    private String address;
    private LocalDate dateOfBirth;
    private String emergencyContact;
}
