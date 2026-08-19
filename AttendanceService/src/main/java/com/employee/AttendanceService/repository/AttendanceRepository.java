package com.employee.AttendanceService.repository;

import com.employee.AttendanceService.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    // Used in logout to find the active record
    Optional<Attendance> findByEmployeeIdAndCheckOutTimeIsNull(String employeeId);

    // Finds if there is an ongoing session for this employee
//    boolean existsByEmployeeIdAndCheckOutTimeIsNull(String employeeId);
    boolean existsByEmployeeIdAndCheckOutTimeIsNullAndCheckInTimeAfter(String employeeId, LocalDateTime time);

    // Finds today's active check-in session
    Optional<Attendance> findByEmployeeIdAndCheckOutTimeIsNullAndCheckInTimeAfter(String employeeId, LocalDateTime time);

    // Fetches all logs from Monday 12:00 AM up to the current moment
    List<Attendance> findByEmployeeIdAndCheckInTimeAfterOrderByCheckInTimeDesc(String employeeId, LocalDateTime startOfWeek);

    @Query(
            value =
                    "SELECT SUM(total_work_min) "+
                            "FROM Attendance "+
                            "WHERE employee_id = :employeeId "+
                            "AND check_in_time BETWEEN :fromDate AND :toDate ",
            nativeQuery = true
    )
    Optional<Long> getTotalWorkMin(@Param("employeeId") String employeeId, @Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

    @Query(
            value = "SELECT * FROM attendance " +
            "WHERE employee_id = :employeeId " +
            "AND (check_in_time >= :startOfDay AND check_in_time <= :endOfDay)",
            nativeQuery = true
    )
    Optional<List<Attendance>> findTodayAttendanceByEmployeeId(
            @Param("employeeId") String employeeId,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    List<Attendance> findByCheckInTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findByCheckInTimeBetweenOrderByCheckInTimeAsc(LocalDateTime startOfDay, LocalDateTime endOfDay);

    List<Attendance> findByEmployeeIdAndCheckInTimeAfter(String employeeId, LocalDateTime time);

    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId " +
            "AND CAST(a.checkInTime AS date) BETWEEN :fromDate AND :toDate " +
            "ORDER BY a.checkInTime DESC")
    List<Attendance> findAttendanceByEmployeeAndDateRange(
            @Param("employeeId") String employeeId,
            @Param("fromDate") LocalDate fromDate,
            @Param("toDate") LocalDate toDate
    );

    @Query("SELECT a FROM Attendance a WHERE a.checkOutTime IS NULL AND a.checkInTime < :startOfToday")
    List<Attendance> findPendingCheckoutsBefore(@Param("startOfToday") LocalDateTime startOfToday);

    Optional<Attendance> findFirstByEmployeeIdAndCheckInTimeAfterOrderByCheckInTimeDesc(String employeeId, LocalDateTime time);

    Optional<Attendance> findFirstByEmployeeIdAndCheckInTimeBetweenOrderByCheckInTimeDesc(
            String employeeId,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime
    );

    boolean existsByEmployeeIdAndCheckInTimeBetween(String employeeId, LocalDateTime start, LocalDateTime end);


//    Optional<Attendance> findByEmployeeIdAndCheckoutDateTimeBetween(
//            String employeeId,
//            LocalDateTime startOfDay,
//            LocalDateTime endOfDay
//    );

//        @Query(value =
//            "SELECT SUM(total_work_min) "+
//            "FROM Attendance "+
//                    "WHERE employee_id = :employeeId "+
//                    "AND EXTRACT(WEEK FROM check_in_time) = :currentWeek "+
//                    "AND EXTRACT(YEAR FROM check_in_time) = :currentYear",
//            nativeQuery = true
//    )
//    Optional<Long> getTotalWorkMin(@Param("employeeId") Long employeeId, @Param("currentWeek") int currentWeek, @Param("currentYear") int currentYear);

//    @Query("SELECT a FROM Attendance a WHERE a.employeeId = :employeeId AND a.employeeStatus = 'ONLINE'")
//    Optional<Attendance> findEmployeeByEmployeeId(@Param("employeeId") String employeeId);
}
