package com.employee.AdminService.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApplicantDetailsRequest {
    @NotBlank(message = "Please provide candidate first name")
    private String candidateFirstName;

    private String candidateMiddleName;

    @NotBlank(message = "Please provide candidate last name")
    private String candidateLastName;

    @NotBlank(message = "Please provide candidate father name")
    private String fatherName;

    @NotBlank(message = "Please provide candidate mother name")
    private String motherName;

    @NotBlank(message = "Please provide marital status")
    private String maritalStatus;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit Indian number")
    private String mobileNumber;

    @Email(message = "Please provide a valid email address")
    private String gmailId;

    @NotBlank(message = "Please provide current work status")
    private String currentWorkStatus;

    private String currentLastCtc;

    private String expectation;

    private String noticePeriod;

    @NotBlank(message = "Please provide relocation status")
    private String relocation;

    @NotBlank(message = "Please provide interview status")
    private String virtualInterviewStatus;
}
