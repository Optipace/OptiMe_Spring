package com.employee.LeaveService.dto.request;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
