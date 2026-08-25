package com.employee.EmployeeProfileService.dto.response;

import com.employee.EmployeeProfileService.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfEmployeeResponse {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private Long designationId;
    private Long workTypeId;
    private String officeId;
    private String officeName;
    private RoleEnum role;
}
