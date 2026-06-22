package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.model.Office;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OfficeRepository extends JpaRepository<Office, String> {
}
