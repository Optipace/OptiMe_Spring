package com.employee.EmployeeProfileService.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddBankAccountRequest {
    @NotNull(message = "Employee id cannot be null")
    private Long employeeId;

    @NotBlank(message="Account number cannot be blank")
    private String accountNumber;

    @NotBlank(message="IFSC code cannot be blank")
    @Pattern(
            regexp="^[A-Z]{4}0[A-Z0-9]{6}$",
            message="Invalid IFSC code"
    )
    private String ifscCode;

    @NotBlank(message = "Bank name cannot be blank")
    private String bankName;

    private String branchName;

    private Boolean active;
}
