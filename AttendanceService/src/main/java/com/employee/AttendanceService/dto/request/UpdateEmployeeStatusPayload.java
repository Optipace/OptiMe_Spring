package com.employee.AttendanceService.dto.request;

import com.employee.AttendanceService.enums.EmployeeAccountStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateEmployeeStatusPayload {
    private Long employeeId;
    private EmployeeAccountStatus employeeAccountStatus;
}
