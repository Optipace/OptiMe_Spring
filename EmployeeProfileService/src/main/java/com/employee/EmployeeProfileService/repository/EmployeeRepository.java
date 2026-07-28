package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.employeeId = :employeeId")
    Optional<Employee> findEmployeeByEmployeeId(@Param("employeeId") String employeeId);

    boolean existsByEmployeeId(String employeeId);

    @Query("SELECT e.employeeId FROM Employee e WHERE e.accountStatus = 'ACTIVE'")
    List<String> findActiveEmployeeIds();
}
