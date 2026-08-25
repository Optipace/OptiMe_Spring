package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.management.relation.Role;

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
