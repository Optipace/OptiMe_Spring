package com.employee.EmployeeProfileService.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UpdateEmployeeRequest {
    @NotNull(message = "Employee id must not be null")
    private String employeeId;

    private String currentAddress;

    @Pattern(
            regexp = "^[0-9]{10}$",
            message = "Emergency contact must be a 10 digit number"
    )
    private String emergencyContact;

    @Email(message = "Invalid email format")
    private String personalEmail;

    private String bloodGroup;
}
