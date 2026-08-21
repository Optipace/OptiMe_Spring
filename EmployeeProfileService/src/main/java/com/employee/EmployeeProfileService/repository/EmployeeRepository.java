package com.employee.EmployeeProfileService.repository;

import com.employee.EmployeeProfileService.enums.RoleEnum;
import com.employee.EmployeeProfileService.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    @Query("SELECT e FROM Employee e WHERE e.userId = :employeeId")
    Optional<Employee> findEmployeeByUserId(@Param("employeeId") Long employeeId);

    @Query("SELECT e FROM Employee e WHERE e.employeeId = :employeeId")
    Optional<Employee> findEmployeeByEmployeeId(@Param("employeeId") String employeeId);

    boolean existsByEmployeeId(Long employeeId);

//    @Query("SELECT e.employeeId FROM Employee e WHERE e.accountStatus = 'ACTIVE'") // <- can use this or next
    @Query("SELECT e.id FROM Employee e WHERE e.profileStatus >= 6 AND e.role NOT IN('ADMIN')")
    List<Long> findActiveEmployeeIds();


    // 1. Fetch with pagination support (Recommended for your Pageable controllers)
    Page<Employee> findByRole(RoleEnum role, Pageable pageable);

    // 2. Fetch as a plain list (If you don't need pagination)
    List<Employee> findByRole(RoleEnum role);
}
