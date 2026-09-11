package com.employee.AuthService.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeProfileResponse {

    private Long id;
    private String employeeName;
    private String employeeId;
    private String contact;
    private String emailId;
    private String accountStatus;
    private String dailyStatus;
    private String role;
    private String gender;
    private String employeeProfilePath;
    private String permanentAddress;
    private String currentAddress;
    private LocalDate dateOfBirth;
    private String emergencyContact;
    private LocalDate dateOfJoining;
    private LocalDate dateOfRelieving;
    private LocalDate dateOfConfirmation;
    private int profileStatus;
    private String bloodGroup;

    // Change these from String to their respective object models/DTOs
    private WorkTypeDto workType;
    private DesignationDto designation;
    private StatusDto status;

    private Long officeId;
    private Long userId;

    // Inner or separate DTO classes matching the incoming JSON structure
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkTypeDto {
        private Long id;
        private String name;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class DesignationDto {
        private Long id;
        private String designation;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class StatusDto {
        private Long id;
        private String status;
    }
}