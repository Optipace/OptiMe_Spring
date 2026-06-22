package com.employee.EmployeeProfileService.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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

    @NotBlank
    @Column(nullable = false, unique = true)
    private String officeName;

    @NotBlank
    @Column(nullable = false, length = 13)
    private String latitude;

    @NotBlank
    @Column(nullable = false, length = 14)
    private String longitude;

    @NotBlank
    @Column(nullable = false, unique = true)
    private String hrEmpId;

    @NotBlank
    @Column(nullable = false)
    private String address;

    @NotBlank
    @Column(nullable = false, length = 13)
    private String contact;

    @NotBlank
    @Column(nullable = false)
    private String googleMap;

}
