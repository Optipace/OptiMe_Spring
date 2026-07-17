package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.dto.response.ApiResponse;
import com.employee.LeaveService.dto.response.LeaveResponse;
import com.employee.LeaveService.dto.response.LeaveTypeResponse;
import com.employee.LeaveService.model.Leave;
import com.employee.LeaveService.model.LeaveType;
import com.employee.LeaveService.repository.LeaveRepository;
import com.employee.LeaveService.repository.LeaveTypeRepository;
import com.employee.LeaveService.service.LeaveInternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class LeaveInternalServiceImpl implements LeaveInternalService {

    private final LeaveRepository leaveRepository;

    private final ModelMapper modelMapper;

    private final LeaveTypeRepository leaveTypeRepository;

    @Override
    public ApiResponse<?> getAllAppliedLeaves() {

        List<Leave> leaves = leaveRepository.findByFromDateGreaterThanEqual(LocalDate.now());
        log.info("Fetching the applied leaves list from today onwards");

        List<LeaveResponse> leaveResponses = leaves.stream()
                .map(l -> {
                    if(l.getApprovedBy() == null){
                         l.setApprovedBy("No one approved Yet");
                        return modelMapper.map(l, LeaveResponse.class);
                    }else{
                        return modelMapper.map(l, LeaveResponse.class);
                    }
                })
                .toList();
        log.info("Loading list of leaves to the payload for internal server communication");
        return new ApiResponse<>(
                true,
                "List of Leaves",
                leaveResponses,
                LocalDateTime.now(),
                200
        );
    }

    @Override
    public ApiResponse<List<LeaveTypeResponse>> getLeaveTypeList() {
        List<LeaveType> leaveTypeList = leaveTypeRepository.findAll();

        List<LeaveTypeResponse> leaveTypeResponseList = leaveTypeList.stream()
                .map(leaveType -> modelMapper.map(leaveType, LeaveTypeResponse.class))
                .toList();
        return new ApiResponse<>(
                true,
                "List of Leave types",
                leaveTypeResponseList,
                LocalDateTime.now(),
                200
        );
    }


}
