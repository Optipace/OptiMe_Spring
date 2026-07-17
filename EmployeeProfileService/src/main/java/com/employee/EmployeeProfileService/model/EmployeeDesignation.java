package com.employee.EmployeeProfileService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_designation")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeDesignation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee designation cannot be blank")
    @Column(nullable = false, unique = true, length = 100)
    private String designation;
}
