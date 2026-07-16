package com.employee.AdminService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeStatusResponse {
    private Long officeStatusId;
    private String officeStatus;
}
