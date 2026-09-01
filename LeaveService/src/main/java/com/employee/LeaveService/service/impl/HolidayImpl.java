package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.client.AdminClient;
import com.employee.LeaveService.dto.request.PutHolidayRequest;
import com.employee.LeaveService.dto.request.SaveHolidaysRequest;
import com.employee.LeaveService.dto.response.HolidayResponse;
import com.employee.LeaveService.dto.response.OfficeResponse;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.repository.HolidayRepository;
import com.employee.LeaveService.service.HolidaysService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class HolidayImpl implements HolidaysService {

    private final HolidayRepository repository;

    @Autowired
    private final AdminClient adminClient;

    private final ModelMapper modelMapper;

    @Override
    public SingleResponse<?> saveHolidays(List<SaveHolidaysRequest> holidays) {
            List<Holidays> listOfHolidays=new ArrayList<>();
            List<LocalDate> listOfDates=new ArrayList<>();

            if (holidays.isEmpty()) {
                throw new CustomException(null, CustomStatus.INVALID_REQUEST_BODY, 400);
            }
            for (SaveHolidaysRequest holiday:holidays) {
                if (repository.
                        findByHolidayDateAndOfficeId(holiday.getHolidayDate(),holiday.getOfficeId())
                        .isPresent()) {
                    listOfDates.add(holiday.getHolidayDate());
                }
                Holidays singleHolidays=new Holidays(
                        null,
                        holiday.getHolidayDate(),
                        holiday.getHolidayName(),
                        holiday.getDescription(),
                        holiday.getOfficeId(),
                        null
                );

                listOfHolidays.add(singleHolidays);
            }
            if(!listOfDates.isEmpty()){
                throw new CustomException(listOfDates.toString(),
                        CustomStatus.HOLIDAY_ALREADY_PRESENT,409);
            }
            repository.saveAll(listOfHolidays);
            return new SingleResponse<>(CustomStatus.SUCCESS, CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> getHolidaysOnYears(Integer yearFrom,Integer yearTo,Long officeId) {

        LocalDate fromDate=LocalDate.of(yearFrom, 1,1);
        LocalDate toDate=LocalDate.of(yearTo, 12,31);
        log.info(fromDate.toString()+" : "+toDate.toString());
        List<Holidays>holidayData=repository.findByYearsFromTo(fromDate,toDate,officeId).orElse(Collections.emptyList());
        if(holidayData.isEmpty()){
            return new SingleResponse<>(holidayData,CustomStatus.SUCCESS);
        }else{
          List<HolidayResponse> responseList= holidayData.stream()
                  .map((holiday -> {
                      try{
                          HolidayResponse response= modelMapper.map(holiday,HolidayResponse.class);
                          SingleResponse<OfficeResponse> officeResult =  adminClient.getOfficeDetails(holiday.getOfficeId());

                          if (officeResult != null && officeResult.getData() != null) {
                              OfficeResponse officeResponse=modelMapper.map(officeResult.getData(), OfficeResponse.class);
                              response.setOffice(officeResponse);
                          }
                          return response;
                      } catch (Exception e) {
                          throw new CustomException(null, CustomStatus.MICROSERVICE_CALL_FAILED, 400);
                      }
                  })).toList();
            return new SingleResponse<>(responseList,CustomStatus.SUCCESS);
        }
    }
    @Override
    public SingleResponse<?> updateHolidayById(Long id, PutHolidayRequest requestBody){
       Holidays getHoliday= repository.findById(id).orElseThrow(
               ()->new CustomException(null,CustomStatus.HOLIDAY_NOT_FOUND,404)
       );

       getHoliday.setHolidayName(requestBody.getHolidayName());
       getHoliday.setDescription(requestBody.getDescription());
        repository.save(getHoliday);
        return new SingleResponse<>(CustomStatus.SUCCESS,CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> deleteHolidayById(Long id) {
        repository.findById(id).orElseThrow(
                ()->new CustomException(null,CustomStatus.HOLIDAY_NOT_FOUND,404)
        );
            repository.deleteById(id);
            return new SingleResponse<>(
                    "",CustomStatus.SUCCESS);

    }
}
