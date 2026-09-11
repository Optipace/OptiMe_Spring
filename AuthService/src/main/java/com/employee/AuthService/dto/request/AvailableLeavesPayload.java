package com.employee.AuthService.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AvailableLeavesPayload {

    @NotNull(message = "Employee Id is required.")
    private Long employeeId;

    @NotNull(message = "Remaining Leaves is required.")
    private Integer remainingLeaves;
}
