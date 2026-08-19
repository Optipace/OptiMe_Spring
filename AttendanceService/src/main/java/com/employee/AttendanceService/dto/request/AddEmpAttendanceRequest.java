package com.employee.AttendanceService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AddEmpAttendanceRequest {
    @NotBlank(message = "Provide employee ID")
    private String employeeId;

    @NotNull(message = "Check in time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkInTime;

//    @NotNull(message = "Check out time cannot be null")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime checkOutTime;

    @NotNull(message = "Provide attendance type(Work Type) ID")
    private Long attendanceTypeId;

    @NotBlank(message = "Remarks cannot be blank")
    private String remarks;
}
