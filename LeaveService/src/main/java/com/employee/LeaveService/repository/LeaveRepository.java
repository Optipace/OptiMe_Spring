package com.employee.LeaveService.repository;

import com.employee.LeaveService.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByFromDateGreaterThanEqual(LocalDate date);

    @Query("SELECT COUNT(l) > 0 FROM Leave l WHERE l.applicantEmployeeId = :employeeId " +
            "AND :today BETWEEN l.fromDate AND l.toDate " +
            "AND l.leaveStatus = 'APPROVED'")
    boolean isEmployeeOnLeaveOnDate(@Param("employeeId") String employeeId, @Param("today") LocalDate today);
//    Optional<Leave> findByEmployeeId(String employeeId);
}
