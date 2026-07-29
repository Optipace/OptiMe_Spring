package com.employee.LeaveService.client;

import com.employee.LeaveService.dto.request.LeaveConfirmationPayload;
import com.employee.LeaveService.dto.request.LeaveEmailPayload;
import com.employee.LeaveService.dto.request.NotificationPayload;
import com.employee.LeaveService.dto.response.ApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "NOTIFICATION-SERVICE", url = "http://localhost:7075")
@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/notifications/internal/send")
    void sendPrivateNotification(@RequestBody NotificationPayload payload);

    @PostMapping("/api/communication/email/sendLeaveEmail")
    ApiResponse<String> sendLeaveEmail(@RequestBody LeaveEmailPayload payload);

    @PostMapping("/api/communication/email/sendConfirmLeaveEmail")
    void sendConfirmationLeaveEmail(@RequestBody LeaveConfirmationPayload payload);
}
