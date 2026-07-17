package com.employee.EmployeeProfileService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_status")
@Data
@AllArgsConstructor
@NoArgsConstructor
// Master Details
public class EmployeeStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee status cannot be blank")
    @Column(nullable = false, unique = true, length = 50)
    private String status; // IN_NOTICE_PERIOD,TERMINATED, ON_LEAVE, PERMANENT
}
