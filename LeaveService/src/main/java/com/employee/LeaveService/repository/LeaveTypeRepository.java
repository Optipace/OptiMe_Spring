package com.employee.LeaveService.repository;

import com.employee.LeaveService.enums.LeaveTypeEnum;
import com.employee.LeaveService.model.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
    Optional<LeaveType> findByLeaveType(LeaveTypeEnum leaveType);
}
