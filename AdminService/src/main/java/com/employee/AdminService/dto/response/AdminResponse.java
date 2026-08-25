package com.employee.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminResponse {
    private Long employeeId;
    private String employeeName;
    private String contact;
    private String emailId;
    private Long designationId;
    private Long workTypeId;
    private OfficeResponse officeResponse;
}
