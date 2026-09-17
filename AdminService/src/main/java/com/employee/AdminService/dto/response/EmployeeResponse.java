package com.employee.AdminService.dto.response;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeName;
    private String employeeId;
    private Long userId;
    private String contact;
    private String emailId;
    private Long designationId;
    private String dailyStatus;
    private String role;
    private String gender;
    private String accountStatus;
    private int isDisContinued;
    //    private String employeeProfilePath;
    private String permanentAddress;
    private String currentAddress;
    private String dateOfBirth;
    private String dateOfJoining;
    private String emergencyContact;
    private Long workTypeId;
    private int profileStatus;
    private String attendanceStatus;
    private Long employeeStatusId;
    private OfficeResponse office;
    private String image;
}
