package com.employee.LeaveService.service.impl;

import com.employee.LeaveService.dto.request.SaveHolidaysRequest;
import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.employee.LeaveService.exception.CustomException;
import com.employee.LeaveService.model.Holidays;
import com.employee.LeaveService.repository.HolidayRepository;
import com.employee.LeaveService.service.HolidaysService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class HolidayImpl implements HolidaysService {

    private final HolidayRepository repository;

    @Override
    public SingleResponse<?> saveHolidays(List<SaveHolidaysRequest> holidays) {
            List<Holidays> listOfHolidays=new ArrayList<>();
            List<LocalDate> listOfDates=new ArrayList<>();

            if (holidays.isEmpty()) {
                throw new CustomException(null, CustomStatus.INVALID_REQUEST_BODY, 400);
            }
            for (SaveHolidaysRequest holiday:holidays) {
                if (repository.findByHolidayDate(holiday.getHolidayDate()).isPresent()) {
                    listOfDates.add(holiday.getHolidayDate());
                }
                Holidays singleHolidays=new Holidays(
                        null,
                        holiday.getHolidayDate(),
                        holiday.getHolidayName(),
                        holiday.getDescription(),
                        null

                );

                listOfHolidays.add(singleHolidays);
            }
            if(!listOfDates.isEmpty()){
                throw new CustomException(listOfDates.toString(),
                        CustomStatus.HOLIDAY_ALREADY_PRESENT,409);
            }

            return new SingleResponse<>(repository.saveAll(listOfHolidays),
                    CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<?> getHolidaysOnYears(Integer yearFrom,Integer yearTo) {

        LocalDate fromDate=LocalDate.of(yearFrom, 1,1);
        LocalDate toDate=LocalDate.of(yearTo, 12,31);
        log.info(fromDate.toString()+" : "+toDate.toString());
        return new SingleResponse<>(repository.findByYearsFromTo(fromDate,toDate),CustomStatus.SUCCESS);
    }
    @Override
    public SingleResponse<?> updateHolidayById(Long id,SaveHolidaysRequest requestBody){
       Holidays getHoliday= repository.findById(id).orElseThrow(
               ()->new CustomException(null,CustomStatus.HOLIDAY_NOT_FOUND,404)
       );
       Holidays updatedHoliday =new Holidays(
               getHoliday.getId(),
               getHoliday.getHolidayDate(),
               requestBody.getHolidayName(),
               requestBody.getDescription(),
               getHoliday.getCreatedAt()
       );
        return new SingleResponse<>(repository.save(updatedHoliday),CustomStatus.SUCCESS);
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
