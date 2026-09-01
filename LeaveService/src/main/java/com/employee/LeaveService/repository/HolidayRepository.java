package com.employee.LeaveService.repository;

import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends JpaRepository<Holidays,Long> {
    @Query("SELECT h from Holidays h where h.holidayDate = :holidayDate"+
            " AND h.officeId = :officeId"
    )
    Optional<Holidays> findByHolidayDateAndOfficeId(LocalDate holidayDate,Long officeId);

    @Query("SELECT h from Holidays h where h.holidayDate >= :fromDate"+
            " AND h.holidayDate <= :toDate"+
            " AND h.officeId = :officeId"
    )
    Optional<List<Holidays>> findByYearsFromTo(LocalDate fromDate,LocalDate toDate,Long officeId);

}
