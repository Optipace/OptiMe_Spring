package com.employee.LeaveService.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "leave_type")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LeaveType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @NotBlank(message = "Leave Type cannot be blank")
    @Column(nullable = false, length = 100, unique = true)
    private String leaveType;

    @Column(nullable = false)
    private Long totalDays;
}
