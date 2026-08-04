package com.employee.AttendanceService.client;

import com.employee.AttendanceService.dto.request.NotificationPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "COMMUNICATION-SERVICE", url = "http://localhost:7075")
@FeignClient(name = "COMMUNICATION-SERVICE")
public interface CommunicationClient {

    @PostMapping("/api/notifications/internal/send")
    void sendPrivateNotification(@RequestBody NotificationPayload payload);
}
