package com.employee.AdminService.dto.response;

import com.employee.AdminService.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmittedApplicationResponse {
    private Long id;
    private String candidateFirstName;
    private String candidateMiddleName;
    private String candidateLastName;
    private String fatherName;
    private String motherName;
    private String maritalStatus;
    private String mobileNumber;
    private String gmailId;
    private String currentWorkStatus;
    private String currentLastCtc;
    private String expectation;
    private String noticePeriod;
    private String relocation;
    private String virtualInterviewStatus;
    private ApplicationStatus applicationStatus;
}
