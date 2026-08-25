package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.OfficeResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.enums.CustomStatus;
import com.employee.AdminService.enums.OfficeStatus;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.model.Office;
import com.employee.AdminService.repository.OfficeRepository;
import com.employee.AdminService.service.InternalService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class InternalServiceImpl implements InternalService {
    private final OfficeRepository officeRepository;
    private final ModelMapper modelMapper;
    @Override
    public SingleResponse<List<OfficeResponse>> getOfficeList() {
        log.info("Requested of office list");
        List<Office> officeList = officeRepository.findAll();

        List<OfficeResponse> officeResponseList = officeList.stream()
                .sorted(Comparator.comparing(Office::getOfficeName, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(o -> modelMapper.map(o,OfficeResponse.class))
                .toList();
//        ListOfOfficeResponse officeResponsesList = new ListOfOfficeResponse(officeResponseList);

        log.info("Returning office list {}",officeResponseList);
        return new SingleResponse<>(
                officeResponseList,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<OfficeResponse> getOfficeDetailsByOfficeId(Long officeId) {
        log.info("Requested office details for office Id {}", officeId);
        Office office = officeRepository.findById(officeId)
                .orElseThrow(() -> new CustomException(null, CustomStatus.OFFICE_NOT_FOUND, 404));
        if(office.getOfficeStatus().equals(OfficeStatus.INACTIVE)){
            log.info("The details for office Id {} is INACTIVE so returning null", officeId);
            return new SingleResponse<>(
                    null,
                    CustomStatus.SUCCESS
            );
        }
        OfficeResponse response = modelMapper.map(office, OfficeResponse.class);
        log.info("Returning office details for office Id {}",office.getId());
        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<?> getOfficeNames() {
        List<Office> officeList = officeRepository.findAll();

        List<String> officeNames = officeList.stream()
                .filter(office -> !OfficeStatus.INACTIVE.equals(office.getOfficeStatus()))
                .map(Office::getOfficeName)
                .toList();
        return new SingleResponse<>(
                officeNames,
                CustomStatus.SUCCESS
        );
    }


}
