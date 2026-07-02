package com.employee.LeaveService.dto.request;

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

    @NotNull(message = "Date must be in YYYY-MM-DD format and it shouldn't be blank")
    private LocalDate fromDate;

    @NotNull(message = "Date must be in YYYY-MM-DD format and it shouldn't be blank")
    private LocalDate toDate;

    @NotBlank(message = "Provide a valid reason")
    private String reason;

}
