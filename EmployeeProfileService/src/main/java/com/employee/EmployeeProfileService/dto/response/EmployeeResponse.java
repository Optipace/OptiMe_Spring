package com.employee.EmployeeProfileService.dto.response;

import com.employee.EmployeeProfileService.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeResponse {
    private String employeeName;
    private String contact;
    private String employeeId;
    private String emailId;
    private EmployeeDesignationEnum employeeDesignation;
    private GenderEnum gender;
    private String address;
    private OfficeResponse office;
}
