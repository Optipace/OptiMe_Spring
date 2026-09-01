package com.employee.LeaveService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Table(name = "working_saturday")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WorkingSat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate workingDate;

    @NotNull(message = "Office id is required")
    @Column(nullable = false)
    private Long officeId;

    private Date createdOn;

    @PrePersist
    protected void onCreate(){
        createdOn=new Date();
    }
}
