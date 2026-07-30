package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.NotificationClient;
import com.employee.AdminService.dto.request.InterviewPayload;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.enums.CustomStatus;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.service.InterviewService;
import com.employee.AdminService.util.InterviewUtil;
import feign.FeignException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

@AllArgsConstructor
@Service
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewUtil interviewUtil;

    private final NotificationClient notificationClient;

    private final ObjectMapper objectMapper;

    private final String url = "https://www.optipace.in/";

    @Override
    public SingleResponse<String> generateToken(InterviewRequest request) {
        String token = interviewUtil.generateToken(request.getEmailId(), request.getRole());

        InterviewPayload payload = new InterviewPayload(
                request.getEmailId(),
                token,
                url
        );

        try {
            notificationClient.sendInterviewEmail(payload);
            log.info("Triggered account created email");
            log.info("Communication service is called to send Interview email");

        } catch (FeignException e) {
            String rawErrorJson = e.contentUTF8();
            String cleanErrorMessage = "Microservice call failed";

            try {
                JsonNode errorNode = objectMapper.readTree(rawErrorJson);
                if (errorNode.has("message")) {
                    cleanErrorMessage = errorNode.get("message").asText();
                } else {
                    cleanErrorMessage = rawErrorJson;
                }
            } catch (Exception parseException) {
                // If the error isn't JSON, just return the raw string
                cleanErrorMessage = rawErrorJson;
            }
            throw new CustomException(cleanErrorMessage, HttpStatus.valueOf(e.status()));
        }

        return new SingleResponse<>(
                null,
                CustomStatus.SUCCESS
        );
    }
}
