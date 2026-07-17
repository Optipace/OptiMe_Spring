package com.employee.LeaveService.dto.request;

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
public class LeaveRequest {

    @NotBlank(message = "Authority employee Id is mandatory")
    private String authorityEmployeeId;

    @NotNull(message = "From Date is required")
    @FutureOrPresent(message = "From Date must be today or future date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;

    @NotNull(message = "To Date is required")
    @FutureOrPresent(message = "To Date must be today or future date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate toDate;

    @NotBlank(message = "Provide a valid reason")
    private String reason;

    @NotNull(message = "Provide a leave type")
    private Long leaveTypeId;

}
