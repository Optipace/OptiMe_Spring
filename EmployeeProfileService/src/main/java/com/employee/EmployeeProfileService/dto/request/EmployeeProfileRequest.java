package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.EmployeeDesignationEnum;
import com.employee.EmployeeProfileService.enums.GenderEnum;
import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.enums.WorkTypeEnum;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileRequest {
        private String employeeId;
        private String userName;
        private String contact;
        private String emailId;
        private EmployeeDesignationEnum designation;
        private LocalDate dateOfBirth;
        private RoleEnum role;
        private GenderEnum gender;
        private WorkTypeEnum workType;
        private String officeId;
        private LocalDate dateOfJoining;
        private String permanentAddress;
}
