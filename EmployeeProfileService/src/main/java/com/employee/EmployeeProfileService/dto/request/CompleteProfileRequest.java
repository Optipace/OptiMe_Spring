package com.employee.EmployeeProfileService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
public class CompleteProfileRequest {
    private String employeeId;
    private String currentAddress;
    private String emergencyContact;
    private String bloodGroup;
}
