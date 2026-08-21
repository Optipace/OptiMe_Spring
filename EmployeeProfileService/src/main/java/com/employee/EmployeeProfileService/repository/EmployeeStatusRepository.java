package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.model.EmployeeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EmployeeStatusRepository extends JpaRepository<EmployeeStatus, Long> {
    Optional<EmployeeStatus> findByStatus(String status);
}
