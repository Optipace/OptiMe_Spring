package com.employee.LeaveService.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PutHolidayRequest {

    @NotBlank(message = "Holiday name is required")
    private String holidayName;

    private String description;
}
