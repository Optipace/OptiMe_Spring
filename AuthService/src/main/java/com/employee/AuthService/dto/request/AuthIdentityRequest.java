package com.employee.AuthService.dto.request;

import com.employee.AuthService.enums.RoleEnum;
import lombok.Data;

@Data
public class AuthIdentityRequest {
    private String employeeId;
    private String emailId;
    private String contact;
    private RoleEnum role;
    private String createdBy;
}
