package com.employee.LeaveService.model;

import com.employee.LeaveService.enums.LeaveStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leave")
public class Leave {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private Long id;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "applied_on", nullable = false)
    private LocalDateTime appliedOn;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "approved_by")
    private String approvedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_status", nullable = false)
    private LeaveStatusEnum leaveStatus = LeaveStatusEnum.PENDING;

    @NotBlank(message = "Provide employee Id")
    @Column(name = "employee_id", nullable = false, length = 12)
    private String employeeId;

    @NotBlank(message = "Employee name required")
    @Column(name = "employee_name", nullable = false, length = 50)
    private String employeeName;

}
