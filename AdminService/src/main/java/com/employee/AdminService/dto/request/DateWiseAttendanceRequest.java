package com.employee.AdminService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DateWiseAttendanceRequest {
    @NotNull(message = "From Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @NotNull(message = "To Date is required")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    @NotNull(message = "Employee ID required")
    private Long employeeId;
}
