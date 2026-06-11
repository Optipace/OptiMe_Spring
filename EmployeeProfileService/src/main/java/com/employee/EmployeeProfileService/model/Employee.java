package com.employee.EmployeeProfileService.model;

import com.employee.EmployeeProfileService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "employee")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    @NotBlank
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

    @Enumerated(EnumType.STRING)
    private EmployeeDesignationEnum designation;

    @Enumerated(EnumType.STRING)
    private EmployeeStatusEnum employeeStatus;

    @Enumerated(EnumType.STRING)
    @NotNull
    private GenderEnum gender;

    @Column(nullable = false)
    @NotBlank
    private String address;

    @Column(name = "office_id")
    private Long office;

//    @ManyToOne(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST,CascadeType.MERGE})
//    @JoinColumn(name = "location_id" ,nullable = false)
//    @NotNull
//    @JsonBackReference
//    private Location location;
}
