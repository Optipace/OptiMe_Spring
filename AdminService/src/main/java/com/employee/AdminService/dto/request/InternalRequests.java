package com.employee.AdminService.dto.request;

import com.employee.AdminService.enums.EmployeeDesignationEnum;
import com.employee.AdminService.enums.GenderEnum;
import com.employee.AdminService.enums.RoleEnum;
import com.employee.AdminService.enums.WorkTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

public class InternalRequests {
    // Payload strictly for Auth Service
    @Data
    @AllArgsConstructor
    public static class AuthIdentityPayload {
        private String employeeId;
        private String emailId;
        private String contact;
        private RoleEnum role;
        private String createdBy; // For auditing
    }

    // Payload strictly for Employee Profile Service
    @Data
    @AllArgsConstructor
    public static class EmployeeProfilePayload {
        private String employeeId;
        private String userName;
        private String contact;
        private String emailId;
        private EmployeeDesignationEnum designation;
        private RoleEnum role;
        private GenderEnum gender;
        private WorkTypeEnum workType;
        private String officeId;
    }
}
