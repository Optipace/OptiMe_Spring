package com.employee.LeaveService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Holidays {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name is required")
    @Column(nullable = false,length = 30)
    private String holidayName;

    private String description;

    private LocalDate createdAt;

    @PrePersist
    protected void onCreate(){
        createdAt=LocalDate.now();
    }
}
