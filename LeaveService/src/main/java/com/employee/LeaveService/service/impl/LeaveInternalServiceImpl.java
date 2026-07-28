package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.EmployeeClient;
import com.employee.LeaveService.dto.request.UpdateLeaveRequest;
import com.employee.LeaveService.dto.response.*;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.Leave;
import com.employee.LeaveService.model.LeaveType;
import com.employee.LeaveService.repository.LeaveRepository;
import com.employee.LeaveService.repository.LeaveTypeRepository;
import com.employee.LeaveService.service.LeaveInternalService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
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

    private final EmployeeClient employeeClient;

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

    @Override
    public boolean isEmployeeOnLeave(String employeeId, LocalDate today) {
        return leaveRepository.isEmployeeOnLeaveOnDate(employeeId, today);
    }

    @Override
    public ApiResponse<?> updateLeave(UpdateLeaveRequest request, String approvedEmployeeId) {
        Leave leave = leaveRepository.findById(request.getLeaveId())
                .orElseThrow(() -> new CustomException("Leave Id not found", HttpStatus.NOT_FOUND));

        ApiResponse<EmployeeResponse> empResponse;
        try{

            log.info("Calling Employee Profile Service for {} details",approvedEmployeeId);
            empResponse = employeeClient.getEmployeeByEmployeeId(approvedEmployeeId);
            log.info("Employee {} details got", approvedEmployeeId);

        }catch (FeignException feignException) {
            String cleanErrorMessage = "Microservice called failed";
            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if (feignException.status() > 0) {
                try {
                    responseStatus = HttpStatus.valueOf(feignException.status());
                } catch (IllegalArgumentException ex) {
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            } else {
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE;
            }
            log.error("Employee Service unreachable");
            throw new CustomException(cleanErrorMessage, responseStatus);
        }

//        if(){
//         // TODO The higher authority can't approve their leave by themselves
//        }
        if(!empResponse.getData().isCanApproveLeave()) {
            throw new CustomException("Only higher authorities can approve leave!", HttpStatus.UNAUTHORIZED);
        }

        if(leave.getApprovedBy() == null || leave.getApprovedBy().isEmpty()){
            leave.setApprovedBy(empResponse.getData().getEmployeeId());
            leave.setLeaveStatus(request.getLeaveStatus());
            leaveRepository.save(leave);
        }
        return new ApiResponse<>(
                true,
                "Leave approved",
                null,
                LocalDateTime.now(),
                200
        );
    }


}
