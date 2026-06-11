package com.employee.AuthService.dto.request;

import com.employee.AuthService.enums.GenderEnum;
import com.employee.AuthService.enums.RoleEnum;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Name is mandatory")
    @Size(min = 3, max = 50, message = "Name should be more than 2 letters")
    private String userName;

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

    @Enumerated(EnumType.STRING)
    private RoleEnum role;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank(message = "Please provide employee designation")
    private String designation;

    private String otpIdentifier;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Gender is required")
    private GenderEnum gender;

    @NotBlank(message = "Address is mandatory and cannot be empty")
    private String address;

//    private int otp;

}
