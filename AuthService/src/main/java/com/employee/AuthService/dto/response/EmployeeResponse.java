package com.employee.AuthService.dto.response;

import com.employee.AuthService.enums.GenderEnum;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmployeeResponse {
    private String employeeName;
    private String employeeId;
    private String contact;
    private String emailId;
    private String employeeDesignation;
    private String dailyStatus;
    private String role;
    private String gender;
//    private String employeeProfilePath;
    private String permanentAddress;
    private String currentAddress;
    private String dateOfBirth;
    private String emergencyContact;
    private Long workTypeId;
    private String profileStatus;
    private OfficeResponse office;
}
