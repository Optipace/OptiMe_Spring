package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeGetProfileResponse {
    private Long id;
    private String employeeName;
    private String employeeId;
    private String contact;
    private String emailId;
    private String employeeDesignation;
    private String dailyStatus;
    private String role;
    private String gender;
    private String permanentAddress;
    private String currentAddress;
    private String dateOfBirth;
    private String emergencyContact;
    private String workType;
    private String profileStatus;
    private OfficeResponse office;
}

