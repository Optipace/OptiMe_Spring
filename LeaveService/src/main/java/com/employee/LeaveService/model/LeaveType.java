package com.employee.LeaveService.model;

import com.employee.LeaveService.enums.LeaveTypeEnum;
import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    private LeaveTypeEnum leaveType;

    @Column(nullable = false)
    private Long totalDays;
}
