package com.employee.AdminService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UpdateOfficeRequest {
    @NotNull(message = "Office Id required")
    private Long id;
    private String officeId;
    private String officeName;
    private String latitude;
    private String longitude;
    private Long hrEmpId;
    private String address;
    private String contact;
    private String googleMap;
}
