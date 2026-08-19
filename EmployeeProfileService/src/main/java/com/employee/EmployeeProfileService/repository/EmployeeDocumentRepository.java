package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.model.Employee;
import com.employee.EmployeeProfileService.model.EmployeeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface EmployeeDocumentRepository extends JpaRepository<EmployeeDocument,Long> {
    Optional<EmployeeDocument> findByEmployee(Employee employee);
}
