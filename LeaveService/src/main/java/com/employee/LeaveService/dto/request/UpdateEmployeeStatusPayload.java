package com.employee.LeaveService.dto.request;

import com.employee.LeaveService.enums.EmployeeStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeStatusPayload {
    private String employeeId;
    private EmployeeStatusEnum employeeStatus;
}
