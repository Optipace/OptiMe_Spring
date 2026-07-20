package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.EmployeeDesignationEnum;
import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
import com.employee.AdminService.enums.WorkTypeEnum;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Employee name is mandatory")
    @Size(min = 3, max = 50, message = "Name should be more than 2 letters")
    private String employeeName;

    @NotBlank(message = "Employee ID must be provided")
    private String employeeId;

    @NotBlank(message = "Contact should not be blank")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Contact must be a valid 10-digit number starting with 6, 7, 8, or 9"
    )
    private String contact;

    @NotBlank(message = "Email Id is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide valid Email-Id"
    )
    private String emailId;

    @NotNull(message = "Role is mandatory")
    private RoleEnum role;

    @NotNull(message = "Please provide DOB or Date of birth must be in YYYY-MM-DD format")
    private LocalDate dateOfBirth;

    @NotNull(message = "Please provide employee designation")
    private Long designationId;

    @NotNull(message = "Gender is mandatory")
    private GenderEnum gender;

    @NotBlank(message = "Office ID is required")
    private String officeId;

    @NotNull(message = "Work type is required")
    private Long workTypeId;

    @NotNull(message = "Date of Joining must be in YYYY-MM-DD format")
    private LocalDate dateOfJoining;

    @NotBlank(message = "Permanent address cannot be empty")
    private String permanentAddress;

}