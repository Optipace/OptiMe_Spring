package com.employee.AuthService.dto.request;

import com.employee.AuthService.enums.RoleEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthIdentityRequest {

    @NotBlank(message = "Employee name is mandatory")
    private String employeeName;

    @NotBlank(message = "Please provide your employee Id to complete registration")
    private String employeeId;

    @NotBlank(message = "Email is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide a valid email"
    )
    private String emailId;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Provide a 10-digit contact number starting with 6,7,8 or 9"
    )
    private String contact;

    @NotNull(message = "Please provide the role")
    private RoleEnum role;

    @NotBlank(message = "Created by identity not found")
    private String createdBy;
}
