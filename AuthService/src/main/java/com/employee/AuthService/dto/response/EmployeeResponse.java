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
    private String employeeProfilePath;
    private String address;
    private String dateOfBirth;
    private String emergencyContact;
    private String workType;
    private String profileStatus;
    private String office;
}
