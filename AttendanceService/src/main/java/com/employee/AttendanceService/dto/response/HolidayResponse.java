package com.employee.AttendanceService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HolidayResponse {

    private Long id;

    private LocalDate holidayDate;

    private String holidayName;

    private String description;

    private Long officeId;

    private Date createdOn;

}
