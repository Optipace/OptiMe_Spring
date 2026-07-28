package com.employee.AdminService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOfficeStatusRequest {
    private String officeId;
    private String status;
}
