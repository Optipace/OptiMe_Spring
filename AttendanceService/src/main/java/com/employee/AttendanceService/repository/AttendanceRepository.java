package com.employee.AttendanceService.repository;

import com.employee.AttendanceService.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    @Query("SELECT a FROM Attendance a WHERE a.employee.employeeId = :employeeId AND a.employeeStatus = 'ONLINE'")
    Optional<Attendance> findEmployeeByEmployeeId(@Param("employeeId") String employeeId);

//    @Query(value =
//            "SELECT SUM(total_work_min) "+
//            "FROM Attendance "+
//                    "WHERE employee_id = :employeeId "+
//                    "AND EXTRACT(WEEK FROM check_in_time) = :currentWeek "+
//                    "AND EXTRACT(YEAR FROM check_in_time) = :currentYear",
//            nativeQuery = true
//    )
//    Optional<Long> getTotalWorkMin(@Param("employeeId") Long employeeId, @Param("currentWeek") int currentWeek, @Param("currentYear") int currentYear);

    @Query(
            value =
                    "SELECT SUM(total_work_min) "+
                            "FROM Attendance "+
                            "WHERE employee_id = :employeeId "+
                            "AND check_in_time BETWEEN :fromDate AND :toDate ",
            nativeQuery = true
    )
    Optional<Long> getTotalWorkMin(@Param("employeeId") Long employeeId, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query(
            value = "SELECT * FROM attendance " +
            "WHERE employee_id = :employeeId " +
            "AND (check_in_time >= :startOfDay AND check_in_time <= :endOfDay)",
            nativeQuery = true
    )
    Optional<List<Attendance>> findTodayAttendanceByEmployeeId(
            @Param("employeeId") Long employeeId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
}
