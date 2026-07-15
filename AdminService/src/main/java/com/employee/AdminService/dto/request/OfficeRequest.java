package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.OfficeStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfficeRequest {
    @NotBlank(message = "Office Id required")
    private String officeId;
    private String officeName;
    private String latitude;
    private String longitude;
    private String hrEmpId;
    private String address;
    private String contact;
    private String googleMap;
    private OfficeStatus officeStatus;
}
