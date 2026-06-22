package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CompleteRegisterRequest {

    @NotBlank(message = "Name of the employee is mandatory")
    private String employeeName;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Address is mandatory and cannot be empty")
    private String address;

    @NotBlank(message = "DOB is mandatory and should be in (YYYY-MM-DD) format")
    private String dateOfBirth;

    @NotBlank(message = "Emergency contact is mandatory")
    private String emergencyContact;

    @NotBlank(message = "Please provide your employee Id to complete registration")
    private String employeeId;
}
