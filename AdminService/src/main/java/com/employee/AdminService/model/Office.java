package com.employee.AdminService.model;

import com.employee.AdminService.enums.OfficeStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String officeId;

    @NotBlank(message = "Please provide office name")
    @Column(nullable = false, unique = true)
    private String officeName;

    @NotBlank(message = "Provide latitude")
    @Column(nullable = false, length = 13)
    private String latitude;

    @NotBlank(message = "Provide longitude")
    @Column(nullable = false, length = 14)
    private String longitude;

    @NotNull(message = "HR Employee ID cannot be blank")
    @Column(nullable = false, length = 12)
    private Long hrEmpId;

    @NotBlank(message = "Please provide the office address")
    @Column(nullable = false)
    private String address;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit Indian number")
    @Column(name = "contact", length = 10)
    private String contact;

    @NotBlank(message = "Please provide the office google map location")
    @Column(nullable = false)
    private String googleMap;

    @Column(length = 200)
    private String groupLink;

    @Enumerated(EnumType.STRING)
    @Column(name = "office_status", length = 50)
    private OfficeStatus officeStatus;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "office_status_id", nullable = false)
//    private OfficeStatus officeStatus;
}
