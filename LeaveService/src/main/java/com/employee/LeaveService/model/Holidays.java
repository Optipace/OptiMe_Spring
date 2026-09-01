package com.employee.LeaveService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Column(nullable = false)
    private LocalDate holidayDate;

    @NotBlank(message = "Holiday name is required")
    @Column(nullable = false,length = 30)
    private String holidayName;

    private String description;

    @NotNull(message = "Office Id is not provided")
    private Long officeId;

    private Date createdOn;

    @PrePersist
    protected void onCreate(){
        createdOn=new Date();
    }
}
