package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ResetPasswordRequest {
    @NotBlank(message = "Provide email address")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide a valid email Id"
    )
    private String emailId;

    @NotBlank(message = "Please provide OTP for validation")
    private String emailOtp;

    @NotBlank(message = "Contact number is mandatory")
    @Pattern(
            regexp = "$|^[6-9]\\d{9}$",
            message = "Provide a 10-digit contact number starting with 6,7,8,9"
    )
    private String contact;

    @NotBlank(message = "Please provide OTP for validation")
    private String mobileOtp;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
