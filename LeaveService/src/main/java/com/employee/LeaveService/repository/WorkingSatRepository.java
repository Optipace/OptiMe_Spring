package com.employee.LeaveService.repository;

import com.employee.LeaveService.model.WorkingSat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface WorkingSatRepository extends JpaRepository<WorkingSat,Long> {

    @Query("SELECT w FROM WorkingSat w where w.workingDate = :workingDate" +
            " AND w.officeId = :officeId"
    )
    Optional<WorkingSat> findByDateAndOfficeId(LocalDate workingDate, Long officeId);

    Optional<List<WorkingSat>> findByOfficeId(Long officeId);

    @Query("SELECT w FROM WorkingSat w where w.workingDate >= :fromDate AND "+
    "w.workingDate <= :toDate AND w.officeId = :officeId"
    )
    Optional<List<WorkingSat>> findByFromDateToDateOfficeId(LocalDate fromDate,LocalDate toDate,Long officeId);
}
