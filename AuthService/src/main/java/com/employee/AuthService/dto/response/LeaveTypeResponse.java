package com.employee.AuthService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveTypeResponse {
    private Long leaveTypeId;
    private String leaveType;
}
