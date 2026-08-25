package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
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

public class UpdateEmployeeRequest {
    @NotNull(message = "Employee id must not be null")
    private String employeeId;

    private String employeeName;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Contact must be a 10 digit number"
    )
    private String contact;

    @Email(message = "Invalid email format")
    private String emailId;

    private RoleEnum role;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private Long designationId;

    private GenderEnum gender;

    private Long officeId;

    private Long workTypeId;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfJoining;

    private String permanentAddress;
}
