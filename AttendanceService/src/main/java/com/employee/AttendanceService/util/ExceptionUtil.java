package com.employee.AttendanceService.util;

import com.employee.AttendanceService.enums.CustomStatus;
import com.employee.AttendanceService.exception.CustomException;
import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@Component
@Slf4j
public record ExceptionUtil(ObjectMapper objectMapper) {

    public CustomException feignExceptionHandler(FeignException exception) {
        String rawErrorJson = exception.contentUTF8();
        log.error("Feign Exception:", rawErrorJson);
        String cleanErrorMessage = "Microservice call failed";
        int extractedErrorCode = -100; // Defaults to MICROSERVICE_CALL_FAILED code

        try {
            JsonNode errorNode = objectMapper.readTree(rawErrorJson);

            // Navigate inside the nested "response" block
            if (errorNode.has("response")) {
                JsonNode responseNode = errorNode.get("response");
                if (responseNode.has("message")) {
                    cleanErrorMessage = responseNode.get("message").asText();
                }
                if (responseNode.has("code")) {
                    extractedErrorCode = responseNode.get("code").asInt();
                }
            } else if (errorNode.has("message")) {
                cleanErrorMessage = errorNode.get("message").asText();
            }
        } catch (Exception parseException) {
            cleanErrorMessage = rawErrorJson;
        }

        int httpStatusValue = (exception.status() > 0) ? exception.status() : HttpStatus.INTERNAL_SERVER_ERROR.value();

        // Map the integer code to the correct Enum instance safely
        CustomStatus status = CustomStatus.fromCode(extractedErrorCode);

        // Pass the clean extracted message to CustomException
        return new CustomException(null, status, httpStatusValue);

    }
}
