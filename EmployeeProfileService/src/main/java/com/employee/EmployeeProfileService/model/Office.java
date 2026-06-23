package com.employee.EmployeeProfileService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "office")
public class Office {

    @Id
    private String id;

    @NotBlank(message = "Please provide office name")
    @Column(nullable = false, unique = true)
    private String officeName;

    @NotBlank(message = "Provide latitude")
    @Column(nullable = false, length = 13)
    private String latitude;

    @NotBlank(message = "Provide longitude")
    @Column(nullable = false, length = 14)
    private String longitude;

    @NotBlank(message = "HR Employee ID cannot be blank")
    @Column(unique = true, nullable = false, length = 12)
    private String hrEmpId;

    @NotBlank(message = "Please provide the office address")
    @Column(nullable = false)
    private String address;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Emergency contact must be a valid 10-digit Indian number")
    @Column(name = "contact", length = 10)
    private String contact;

    @NotBlank(message = "Please provide the office google map location")
    @Column(nullable = false)
    private String googleMap;

}
