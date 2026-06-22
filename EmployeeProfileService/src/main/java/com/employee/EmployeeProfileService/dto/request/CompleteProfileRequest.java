package com.employee.EmployeeProfileService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CompleteProfileRequest {
    private String employeeId;
    private String employeeName;
    private String address;
    private String dateOfBirth;
    private String emergencyContact;
}
