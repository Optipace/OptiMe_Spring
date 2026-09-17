package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.RoleEnum;
import com.employee.AdminService.enums.UserStatusEnum;
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
    private UserStatusEnum accountStatus;
    private Long createdBy;
}
