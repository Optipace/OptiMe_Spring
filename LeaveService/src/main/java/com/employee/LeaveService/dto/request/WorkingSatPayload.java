package com.employee.LeaveService.dto.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkingSatPayload {

    @NotEmpty(message = "Working date is required")
    private List<LocalDate> workingDate;

    @NotNull(message = "Office id is required")
    private Long officeId;
}
