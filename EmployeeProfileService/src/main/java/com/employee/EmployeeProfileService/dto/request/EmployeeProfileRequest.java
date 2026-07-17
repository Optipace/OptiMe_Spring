package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.GenderEnum;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileRequest {
        private String employeeId;
        private String employeeName;
        private String contact;
        private String emailId;
        private Long employeeDesignationId;
        private LocalDate dateOfBirth;
        private RoleEnum role;
        private GenderEnum gender;
        private Long workTypeId;
        private String officeId;
        private LocalDate dateOfJoining;
        private String permanentAddress;
}
