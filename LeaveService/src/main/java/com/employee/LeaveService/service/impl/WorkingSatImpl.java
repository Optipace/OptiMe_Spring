package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.AdminClient;
import com.employee.LeaveService.dto.request.WorkingSatPayload;
import com.employee.LeaveService.dto.response.OfficeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.dto.response.WorkingSatResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.WorkingSat;
import com.employee.LeaveService.repository.WorkingSatRepository;
import com.employee.LeaveService.service.WorkingSatService;
import com.employee.LeaveService.util.ExceptionUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class WorkingSatImpl implements WorkingSatService {

    @Autowired
    private final WorkingSatRepository workingSatRepository;

    @Autowired
    private final AdminClient adminClient;

    private final ExceptionUtil exceptionUtil;

    private ModelMapper modelMapper;

    @Override
    public SingleResponse<?> saveWorkingSaturday(WorkingSatPayload payload) {
        List<LocalDate> duplicateDates = new ArrayList<>();

        // remove duplicate dates in payload
        List<LocalDate> uniqueDates = payload.getWorkingDate().stream()
                .distinct()
                .toList();

        // checking office details is present
        try {
            SingleResponse<OfficeResponse> officeDetail = adminClient.getOfficeDetails(payload.getOfficeId());

            System.out.println(officeDetail.getData());
            if (officeDetail.getData() == null) {
                throw new CustomException(null, CustomStatus.OFFICE_NOT_FOUND, 404);
            }
        } catch (FeignException e){
            log.info("FeignException error : {}", e.contentUTF8());
            throw exceptionUtil.feignExceptionHandler(e);
        }

        // checking if date is already present in database
        for (LocalDate workingDate: uniqueDates) {
            boolean workingSatIsPresent = workingSatRepository.findByDateAndOfficeId(workingDate,payload.getOfficeId()).isPresent();
            if(workingSatIsPresent){
                duplicateDates.add(workingDate);
            }
        }

        if(!duplicateDates.isEmpty()){
            String message = duplicateDates.stream().map(LocalDate::toString).collect(Collectors.joining(", "));
            throw new CustomException(message, CustomStatus.DUPLICATE_WORKING_DATES, 400);
        }

        // saving in db
        for (LocalDate workingDate: uniqueDates) {
            WorkingSat editedPayload = new WorkingSat(
                    null,
                    workingDate,
                    payload.getOfficeId(),
                    null
            );
            workingSatRepository.save(editedPayload);
        }

        return new SingleResponse<>(null, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(Long officeId) {
        List<WorkingSat> listOfWorkingSat = workingSatRepository.findByOfficeId(officeId).orElse(Collections.emptyList());

        if(listOfWorkingSat.isEmpty()){
            return new SingleResponse<>(null,CustomStatus.SUCCESS);
        }

        // checking office details is present
        try {
            SingleResponse<OfficeResponse> officeDetail = adminClient.getOfficeDetails(officeId);
            if (officeDetail.getData() == null) {
                throw new CustomException(null, CustomStatus.OFFICE_NOT_FOUND, 404);
            }

            List<WorkingSatResponse> responseList=listOfWorkingSat.stream()
                    .map(workingSat -> {
                        WorkingSatResponse response =modelMapper.map(workingSat,WorkingSatResponse.class);
                        response.setOffice(officeDetail.getData());
                        return response;
                    }).toList();

            return new SingleResponse<>(responseList,CustomStatus.SUCCESS);

        } catch (FeignException e){
            log.info("FeignException error : {}", e.contentUTF8());
            throw exceptionUtil.feignExceptionHandler(e);
        }

    }


}
