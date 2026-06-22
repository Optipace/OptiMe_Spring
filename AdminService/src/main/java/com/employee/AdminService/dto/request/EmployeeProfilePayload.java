package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.EmployeeDesignationEnum;
import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
import com.employee.AdminService.enums.WorkTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EmployeeProfilePayload {
    private String employeeId;
    private String userName;
    private String contact;
    private String emailId;
    private EmployeeDesignationEnum designation;
    private RoleEnum role;
    private GenderEnum gender;
    private WorkTypeEnum workType;
    private String officeId;
}
