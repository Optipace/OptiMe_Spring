package com.employee.EmployeeProfileService.dto.response;

import com.employee.EmployeeProfileService.enums.IsDiscontinued;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor // TODO CHECK all files using this dto file
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
    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private IsDiscontinued isDisContinued;
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
