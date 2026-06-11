package com.employee.AttendanceService.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeLoginRequest {
//    @NotBlank(message = "Email ID is mandatory")
//    @Pattern(
//            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
//            message = "Provide valid Email-Id"
//    )
//    private String emailId;
//
//    @NotBlank(message = "Please provide your password")
//    private String password;

    @NotBlank(message = "Employee ID is mandatory")
    private String employeeId;

//    @NotBlank(message = "Location should not be blank")
//    private String location;
}
