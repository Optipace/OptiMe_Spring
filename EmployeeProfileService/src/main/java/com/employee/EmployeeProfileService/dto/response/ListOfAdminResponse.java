package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListOfAdminResponse {
    private Long id;
    private String employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private Long designationId;
    private Long workTypeId;
    private String officeId;
}
