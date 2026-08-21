package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListOfAdminInternalResponse {
    private Long employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private Long designationId;
    private Long workTypeId;
    private Long officeId;
}
