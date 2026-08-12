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
public class ApproveLeaveRequest {
    @NotNull(message = "Provide leave Id")
    private Long leaveId;
    private String remarks;
}
