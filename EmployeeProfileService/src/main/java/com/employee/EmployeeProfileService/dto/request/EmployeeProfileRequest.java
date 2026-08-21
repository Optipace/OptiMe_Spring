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
        private Long designationId;
        private RoleEnum role;
        private GenderEnum gender;
        private Long workTypeId;
        private Long officeId;
        private LocalDate dateOfBirth;
        private LocalDate dateOfJoining;
        private String permanentAddress;
        private Long userId;
}
