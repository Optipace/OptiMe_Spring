package com.employee.AttendanceService.model;

import com.employee.AttendanceService.enums.AttendanceStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "check_in_time", nullable = false)
    private LocalDateTime checkInTime;

    @Column(name = "check_out_time")
    private LocalDateTime checkOutTime;

    @Column(name = "total_work_min", nullable = false)
    private Long totalWorkMin;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_status", nullable = false)
    private AttendanceStatusEnum attendanceStatus;

    @NotNull
    @Column(name = "employee_id",nullable = false, length = 12)
    private Long employeeId;

    @NotBlank(message = "Provide latitude")
    @Column(nullable = false, length = 13)
    private String latitude;

    @NotBlank(message = "Provide longitude")
    @Column(nullable = false, length = 14)
    private String longitude;

    private String filePath;

    @NotNull(message = "Provide attendance type")
    @Column(name = "attendance_type_id", nullable = false, length = 50)
    private Long attendanceTypeId;

    @Column(name = "is_admin_modified")
    private boolean isAdminModified = false;

    @Column(name = "admin_remarks")
    private String adminRemarks;

}
