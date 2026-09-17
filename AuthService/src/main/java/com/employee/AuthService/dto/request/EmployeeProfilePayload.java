package com.employee.AuthService.dto.request;

import com.employee.AuthService.enums.UserStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EmployeeProfilePayload {
    private String employeeId;
    private String currentAddress;
    private String emergencyContact;
    private String bloodGroup;
    private Long userId;
    private UserStatusEnum accountStatus;
}
