package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRegisterRequest {

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Address is mandatory and cannot be empty")
    private String currentAddress;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact must be a valid 10-digit Indian number")
    private String emergencyContact;

    @NotBlank(message = "Please provide your employee Id to complete registration")
    private String employeeId;

    @NotBlank(message = "Email Id is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide valid Email-Id"
    )
    private String personalEmail;

    private String bloodGroup;

    @NotBlank(message = "Validation token should not be blank")
    private String validationToken;
}
