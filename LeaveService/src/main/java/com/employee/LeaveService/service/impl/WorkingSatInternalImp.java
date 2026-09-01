package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.repository.WorkingSatRepository;
import com.employee.LeaveService.service.WorkingSatInternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Slf4j
@AllArgsConstructor
public class WorkingSatInternalImp implements WorkingSatInternalService {

    private final WorkingSatRepository workingSatRepository;

    @Override
    public SingleResponse<Boolean> getWorkingSatByDateOfficeId(LocalDate workingDate, Long officeId) {
        Boolean isPresent = workingSatRepository.findByDateAndOfficeId(workingDate,officeId).isPresent();
        return new SingleResponse<>(isPresent, CustomStatus.SUCCESS);
    }
}
