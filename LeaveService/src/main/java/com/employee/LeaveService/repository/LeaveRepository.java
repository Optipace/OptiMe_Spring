package com.employee.LeaveService.repository;

import com.employee.LeaveService.enums.LeaveStatusEnum;
import com.employee.LeaveService.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByFromDateGreaterThanEqual(LocalDate date);

    @Query("SELECT COUNT(l) > 0 FROM Leave l WHERE l.applicantEmployeeId = :employeeId " +
            "AND :today BETWEEN l.fromDate AND l.toDate " +
            "AND l.leaveStatus = 'APPROVED'")
    boolean isEmployeeOnLeaveOnDate(@Param("employeeId") Long employeeId, @Param("today") LocalDate today);

    // Query to find if any existing leave overlaps with the new request dates
    @Query("SELECT COUNT(l) > 0 FROM Leave l WHERE l.applicantEmployeeId = :empId " +
            "AND l.fromDate <= :toDate AND l.toDate >= :fromDate")
    boolean existsOverlappingLeave(@Param("empId") Long employeeId,
                                   @Param("fromDate") LocalDate fromDate,
                                   @Param("toDate") LocalDate toDate);

    // Finds all leaves overlapping the requested date range
    @Query("SELECT l FROM Leave l WHERE l.applicantEmployeeId = :employeeId " +
            "AND l.leaveStatus = 'APPROVED' " +
            "AND ((l.fromDate BETWEEN :startDate AND :endDate) " +
            "OR (l.toDate BETWEEN :startDate AND :endDate) " +
            "OR (:startDate BETWEEN l.fromDate AND l.toDate))")
    List<Leave> findApprovedLeavesInDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    @Query("SELECT l FROM Leave l WHERE l.applicantEmployeeId = :employeeId " +
            "AND l.fromDate >= :startOfYear AND l.toDate <= :endOfYear")
    Optional<List<Leave>> findByApplicantEmployeeIdAndDateRange(
            @Param("employeeId") Long employeeId,
            @Param("startOfYear") LocalDate startOfYear,
            @Param("endOfYear") LocalDate endOfYear
    );

    Optional<List<Leave>> findByApproverEmpId(Long employeeId);

    Optional<List<Leave>> findByLeaveStatus(LeaveStatusEnum status);

    List<Leave> findByLeaveStatusInAndToDateGreaterThanEqual(
            List<LeaveStatusEnum> statuses,
            LocalDate date
    );

    Optional<List<Leave>> findByApplicantEmployeeId(String employeeId);
//    Optional<Leave> findByEmployeeId(String employeeId);
}
