package com.employee.LeaveService.repository;

import com.employee.LeaveService.model.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByFromDateGreaterThanEqual(LocalDate date);

    Optional<Leave> findByEmployeeId(String employeeId);
}
