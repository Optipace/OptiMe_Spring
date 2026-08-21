package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class AuthIdentityPayload {
    private String employeeName;
    private String employeeId;
    private String emailId;
    private String contact;
    private RoleEnum role;
    private Long createdBy;
}
