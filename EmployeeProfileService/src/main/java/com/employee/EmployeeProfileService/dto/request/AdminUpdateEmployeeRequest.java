package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.GenderEnum;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class AdminUpdateEmployeeRequest {
    private String employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private RoleEnum role;
    private GenderEnum gender;
    private String permanentAddress;
    private LocalDate dateOfBirth;
    private LocalDate dateOfJoining;
    private Long workTypeId;
    private Long designationId;
    private Long officeId;
}
