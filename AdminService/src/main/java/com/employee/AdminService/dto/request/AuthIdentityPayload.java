package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthIdentityPayload {
    private String employeeId;
    private String emailId;
    private String contact;
    private RoleEnum role;
    private String createdBy;
}
