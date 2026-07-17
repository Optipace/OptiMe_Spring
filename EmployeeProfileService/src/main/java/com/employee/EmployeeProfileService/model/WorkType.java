package com.employee.EmployeeProfileService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "work_type")
@Data
@AllArgsConstructor
@NoArgsConstructor
// Master Table
public class WorkType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Work type cannot be blank")
    @Column(nullable = false, unique = true, length = 50)
    private String name; // WFH,WFO,HYBRID,OTHERS
}
