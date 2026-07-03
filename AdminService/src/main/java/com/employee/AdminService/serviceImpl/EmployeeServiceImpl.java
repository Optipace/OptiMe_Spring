package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.LeaveClient;
import com.employee.AdminService.dto.response.ApiResponse;
import com.employee.AdminService.dto.response.LeaveResponse;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.service.EmployeeService;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final LeaveClient leaveClient;

    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Override
    public ApiResponse<?> getAllAppliedLeaves() {

        List<LeaveResponse> leaveResponses = null;
        try{
            ApiResponse<List<LeaveResponse>> apiResponse = leaveClient.getAllAppliedLeaves();
            log.info("LEAVE SERVICE CALLED");
            if(apiResponse !=null && apiResponse.getData() != null){
                leaveResponses = apiResponse.getData().stream()
                        .map(l -> modelMapper.map(l, LeaveResponse.class))
                        .toList();
            }
        }catch (FeignException fe){
            String rawErrorJson = fe.contentUTF8();
            String cleanErrorMessage = "Microservices call failed";

            try{
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if(errorNode.has("message")){
                    cleanErrorMessage = errorNode.get("message").toString();
                }else{
                    cleanErrorMessage = rawErrorJson;
                }
            }catch (Exception parseException){
                cleanErrorMessage = rawErrorJson;
            }

            HttpStatus responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            if(fe.status() > 0){
                try{
                    responseStatus = HttpStatus.valueOf(fe.status());
                }catch (IllegalArgumentException ex){
                    responseStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                }
            }else{
                cleanErrorMessage = "Service is unreachable. Please try again later.";
                responseStatus = HttpStatus.SERVICE_UNAVAILABLE; // 503 Status
            }
            throw new CustomException(cleanErrorMessage, responseStatus);
        }
        return new ApiResponse<>(
                true,
                "Applied Leaves",
                leaveResponses,
                LocalDateTime.now(),
                200
        );
    }
}
