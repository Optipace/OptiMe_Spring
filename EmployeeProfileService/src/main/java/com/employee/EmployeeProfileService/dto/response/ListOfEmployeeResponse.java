package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfEmployeeResponse {
    private String employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private String employeeDesignation;
    private String workType;
    private String officeId;
    private String officeName;
}
