package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.AdminClient;
import com.employee.LeaveService.dto.request.PutWorkingSatPayload;
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
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;
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
                .peek(localDate -> {
                    if(localDate.getDayOfWeek() != DayOfWeek.SATURDAY){
                        throw new CustomException(String.valueOf(localDate), CustomStatus.WORKING_SAT_NOT_SAT, 400);
                    }
                })
                .toList();
        log.info(payload.toString());
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
    public SingleResponse<List<WorkingSatResponse>> getWorkingSaturdayByOfficeId(Long officeId, Integer month, Integer year) {
        LocalDate fromDate=LocalDate.of(year,month,1);

        log.info("{}-{}", fromDate, fromDate.with(TemporalAdjusters.lastDayOfMonth()));
        List<WorkingSat> listOfWorkingSat = workingSatRepository.findByFromDateToDateOfficeId(
                fromDate, fromDate.with(TemporalAdjusters.lastDayOfMonth()), officeId
                )
                .orElse(Collections.emptyList());
        log.info(listOfWorkingSat.toString());
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

    @Override
    public SingleResponse<WorkingSatResponse> updateWorkingSaturday(Long id, @Valid PutWorkingSatPayload payload) {
        if(payload.getWorkingDate().getDayOfWeek() != DayOfWeek.SATURDAY){
            throw new CustomException(String.valueOf(payload.getWorkingDate()), CustomStatus.WORKING_SAT_NOT_SAT, 400);
        }
        WorkingSat currentWorkingSat = workingSatRepository.findById(id).orElseThrow(()->
                new CustomException(null,CustomStatus.WORKING_SAT_NOT_FOUND,404));

       boolean isAlreadyPresent=workingSatRepository.findByDateAndOfficeId(payload.getWorkingDate(),currentWorkingSat.getOfficeId()).isPresent();
       if(isAlreadyPresent){
           throw new CustomException(String.valueOf(payload.getWorkingDate()), CustomStatus.DUPLICATE_WORKING_DATES, 400);
       }
        try {
            currentWorkingSat.setWorkingDate(payload.getWorkingDate());
            workingSatRepository.save(currentWorkingSat);
            return new SingleResponse<>(null,CustomStatus.SUCCESS);
        } catch (Exception e){
            log.error(String.valueOf(e));
            throw new CustomException(null,CustomStatus.WORKING_SAT_UPDATE_ERROR,409);
        }
    }

    @Override
    public SingleResponse<?> deleteWorkingSaturday(Long id) {
       workingSatRepository.findById(id).orElseThrow(()->
        new CustomException(null,CustomStatus.WORKING_SAT_NOT_FOUND,404));
        try {
            workingSatRepository.deleteById(id);
            return new SingleResponse<>(null,CustomStatus.SUCCESS);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new CustomException(null,CustomStatus.WORKING_SAT_DELETE_ERROR,409);
        }
    }


}
