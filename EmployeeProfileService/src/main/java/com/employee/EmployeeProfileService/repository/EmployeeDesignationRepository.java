package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.model.EmployeeDesignation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeDesignationRepository extends JpaRepository<EmployeeDesignation, Long> {
}
