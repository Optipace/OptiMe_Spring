package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
