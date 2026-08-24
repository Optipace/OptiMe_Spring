package com.employee.LeaveService.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SaveHolidaysRequest {

    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name is required")
    private String holidayName;

    private String description;}
