package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ValidationRequest {
    @NotBlank(message = "Email is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide a valid email"
    )
    private String email;

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

}
