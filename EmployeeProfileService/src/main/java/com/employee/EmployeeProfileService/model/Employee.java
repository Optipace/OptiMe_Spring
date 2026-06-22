package com.employee.EmployeeProfileService.model;

import com.employee.EmployeeProfileService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.catalina.User;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;

    @Column(nullable = false, unique = true)
    @NotBlank
    private String employeeId;

    @Column(unique = true, nullable = false, length = 13)
    @NotBlank
    private String contact;

    @Column(unique = true, nullable = false, length = 40)
    @NotBlank
    private String emailId;

    @NotBlank
    private String designation;

    private String employeeStatus;

    private String dailyStatus;

    private String role;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GenderEnum gender;

    private String employeeProfilePath;

    private String address;

    private String dateOfBirth;

    private String emergencyContact;

    @NotBlank
    private String workType;

    @Enumerated(EnumType.STRING)
    private ProfileStatusEnum profileStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id")
    private Office office;
}
