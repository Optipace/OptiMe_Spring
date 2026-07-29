package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // TODO CHECK all files using this dto file
public class EmployeeResponse {
    private String employeeName;
    private String employeeId;
    private String contact;
    private String emailId;
    private Long designationId;
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
    private String attendanceStatus;
    private Long employeeStatusId;
    private OfficeResponse office;
    private String image;
}
