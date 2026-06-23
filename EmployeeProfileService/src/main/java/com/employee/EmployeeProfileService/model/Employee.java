package com.employee.EmployeeProfileService.model;

import com.employee.EmployeeProfileService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_name", length = 50)
    private String employeeName;

    @NotBlank(message = "Employee ID cannot be blank")
    @Column(name = "employee_id", unique = true, nullable = false, length = 12)
    private String employeeId;

    @Pattern(
            regexp = "^[6-9]\\d{9}$",
            message = "Contact must be a valid 10 digit number"
    )
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact must be a valid 10-digit Indian number")
    @Column(name = "contact", length = 10)
    private String contact;

    @Email(message = "Please provide a valid email address")
    @Column(name = "email_id", nullable = false, unique = true, length = 50)
    private String emailId;

    @NotNull(message = "Designation is mandatory")
    @Enumerated(EnumType.STRING)
    @Column(name = "designation", length = 30)
    private EmployeeDesignationEnum designation;

    @Enumerated(EnumType.STRING)
    @Column(name = "employee_status", length = 20)
    private EmployeeStatusEnum employeeStatus;

    @Column(name = "daily_status", length = 20)
    private String dailyStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", length = 10)
    private RoleEnum role;

    @Enumerated(EnumType.STRING)
    @NotNull(message = "Gender is mandatory")
    @Column(name = "gender", length = 10)
    private GenderEnum gender;

    private String employeeProfilePath;

    private String address;

    @NotNull(message = "Date of birth must be in YYYY-MM-DD format")
    private LocalDate dateOfBirth;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact must be a valid 10-digit Indian number")
    @Column(name = "emergency_contact", length = 10)
    private String emergencyContact;

    @Enumerated(EnumType.STRING)
    @Column(name = "work_type", length = 20)
    private WorkTypeEnum workType;

//    @Enumerated(EnumType.STRING)
//    private ProfileStatusEnum profileStatus;

    @Column(name = "date_of_joining")
    private LocalDate dateOfJoining;

    @Column(name = "date_of_relieving")
    private LocalDate dateOfRelieving;

    @Column(name = "date_of_confirmation")
    private LocalDate dateOfConfirmation;

    private int status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;
}
