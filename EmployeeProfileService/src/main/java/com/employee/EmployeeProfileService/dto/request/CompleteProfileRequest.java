package com.employee.EmployeeProfileService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class CompleteProfileRequest {
    private String employeeId;
    private String employeeName;
    private String address;
    private LocalDate dateOfBirth;
    private String emergencyContact;
}
