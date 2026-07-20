package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
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
    private String contact;
    private String emailId;
    private Long designationId;
    private RoleEnum role;
    private GenderEnum gender;
    private Long workTypeId;
    private String officeId;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private String permanentAddress;
}
