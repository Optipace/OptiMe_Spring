package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.OfficeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AddNewOfficeRequest {
    @NotBlank(message = "Office id cannot be blank")
    private String officeId;

    @NotBlank(message = "Office name cannot be blank")
    private String officeName;

    @NotBlank(message = "Latitude cannot be blank")
    private String latitude;

    @NotBlank(message = "Longitude cannot be blank")
    private String longitude;

    @NotBlank(message = "HR Emp Id cannot be blank")
    private String hrEmpId;

    @NotBlank(message = "Office Address cannot be blank")
    private String address;

    @NotBlank(message = "Provide office contact number")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact must be a valid 10-digit Indian number")
    @Size(max = 10)
    private String contact;

    @NotBlank(message = "Google map link cannot be blank")
    private String googleMap;

}
