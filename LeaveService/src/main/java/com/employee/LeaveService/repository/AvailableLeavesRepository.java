package com.employee.LeaveService.repository;

import com.employee.LeaveService.model.AvailableLeaves;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AvailableLeavesRepository extends JpaRepository<AvailableLeaves, Long> {
    Optional<AvailableLeaves> findByEmployeeId(Long employeeId);

}
