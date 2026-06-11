package com.employee.EmployeeProfileService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeResponse {
    private Long OfficeId;
    private String officeName;
    private String latitude;
    private String longitude;
    private String hr;
    private String address;
    private String contact;
    private String googleMap;
}
