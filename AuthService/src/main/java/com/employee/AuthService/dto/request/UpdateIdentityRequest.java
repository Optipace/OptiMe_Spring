package com.employee.AuthService.dto.request;

import com.employee.AuthService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class UpdateIdentityRequest {
    private String employeeId;
    private String userName;
    private String emailId;
    private String contact;
    private RoleEnum role;
}
