package com.employee.LeaveService.client;

import com.employee.LeaveService.dto.request.*;
import com.employee.LeaveService.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "COMMUNICATION-SERVICE", url = "http://localhost:7075")
@FeignClient(name = "COMMUNICATION-SERVICE")
public interface CommunicationClient {

    @PostMapping("/api/notifications/internal/send")
    void sendPrivateNotification(@RequestBody NotificationPayload payload);

    @PostMapping("/api/communication/email/sendLeaveEmail")
    ApiResponse<String> sendLeaveEmail(@RequestBody LeaveEmailPayload payload);

    @PostMapping("/api/communication/email/sendConfirmLeaveEmail")
    void sendConfirmationLeaveEmail(@RequestBody LeaveConfirmationPayload payload);

    @PostMapping("/api/communication/email/sendLeaveApprovedEmail")
    void sendLeaveApprovedEmail(@RequestBody LeaveApprovePayload payload);

    @PostMapping("/api/communication/email/sendLeaveRejectedEmail")
    void sendLeaveRejectedEmail(@RequestBody LeaveRejectedPayload payload);
}
