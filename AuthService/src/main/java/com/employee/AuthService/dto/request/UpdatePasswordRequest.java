package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdatePasswordRequest {

    @NotBlank(message = "Old Password cannot be blank")
    private String oldPassword;

    @NotBlank(message = "New Password cannot be blank")
    @Size(min = 8, message = "New Password must be at least 8 characters")
    private String newPassword;
}
