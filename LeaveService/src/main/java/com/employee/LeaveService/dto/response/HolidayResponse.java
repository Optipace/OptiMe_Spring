package com.employee.LeaveService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponse {

    private Long id;

    private LocalDate holidayDate;

    private String holidayName;

    private String description;

    private OfficeResponse office;
}
