package com.employee.LeaveService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelMyLeaveRequest {
    @NotNull(message = "Provide leave Id")
    private Long leaveId;
    @NotBlank(message = "Provide a valid reason for your leave cancellation")
    private String reason;
}
