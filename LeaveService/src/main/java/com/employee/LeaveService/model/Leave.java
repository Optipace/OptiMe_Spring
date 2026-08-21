package com.employee.LeaveService.model;

import com.employee.LeaveService.enums.LeaveStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "from_date", nullable = false)
    private LocalDate fromDate;

    @Column(name = "to_date", nullable = false)
    private LocalDate toDate;

    @Column(name = "applied_on", nullable = false)
    private LocalDateTime appliedOn;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "leave_reason", nullable = false)
    private String leaveReason;

    @Column(name = "wanted_leaves", nullable = false)
    private Integer wantedLeaves;

    @Column(name = "approved_by", length = 20)
    private Long approvedBy;

    @Column(nullable = false, length = 20)
    private Long approverEmpId;

    @Enumerated(EnumType.STRING)
    @Column(name = "leave_status", nullable = false)
    private LeaveStatusEnum leaveStatus = LeaveStatusEnum.PENDING;

    @NotNull(message = "Provide employee Id")
    @Column(name = "applicant_employee_id", nullable = false, length = 20)
    private Long applicantEmployeeId;

//    @NotBlank(message = "Employee name required")
//    @Column(name = "applicant_employee_name", nullable = false, length = 50)
//    private String applicantEmployeeName;

    @ManyToOne
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

}
