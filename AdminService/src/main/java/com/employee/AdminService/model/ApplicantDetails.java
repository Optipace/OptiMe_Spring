package com.employee.AdminService.model;

import com.employee.AdminService.enums.ApplicationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "applicant_details")
public class ApplicantDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Please provide candidate first name")
    @Column(nullable = false, length = 100)
    private String candidateFirstName;

    private String candidateMiddleName;

    @NotBlank(message = "Please provide candidate last name")
    @Column(nullable = false, length = 100)
    private String candidateLastName;

    @NotBlank(message = "Please provide candidate father first name")
    @Column(nullable = false, length = 100)
    private String fatherFirstName;

    private String fatherMiddleName;

    @NotBlank(message = "Please provide candidate father last name")
    @Column(nullable = false, length = 100)
    private String fatherLastName;

    @NotBlank(message = "Please provide candidate mother first name")
    @Column(nullable = false, length = 100)
    private String motherFirstName;

    private String motherMiddleName;

    @NotBlank(message = "Please provide candidate mother last name")
    @Column(nullable = false, length = 100)
    private String motherLastName;

    @NotBlank(message = "Please provide marital status")
    @Column(nullable = false, length = 30)
    private String maritalStatus;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit Indian number")
    @Column(length = 10)
    private String mobileNumber;

    @Email(message = "Please provide a valid email address")
    @Column(nullable = false, length = 50)
    private String gmailId;

    @NotBlank(message = "Please provide current work status")
    @Column(nullable = false, length = 50)
    private String currentWorkStatus;

    private String currentLastCtc;

    private String expectation;

    private String noticePeriod;

    @NotBlank(message = "Please provide relocation status")
    @Column(nullable = false, length = 100)
    private String relocation;

    @NotBlank(message = "Please provide interview status")
    @Column(nullable = false, length = 100)
    private String virtualInterviewStatus;

    @NotNull(message = "Please provide application status")
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
}
