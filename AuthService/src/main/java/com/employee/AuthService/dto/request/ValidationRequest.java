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
    @NotBlank(message = "identifier is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$|^[6-9]\\d{9}$",
            message = "identifier must be a valid email or a 10-digit contact number starting with 6,7,8,9"
    )
    private String identifier;

    @NotBlank(message = "OTP is mandatory")
    private String otp;

}
