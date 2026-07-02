package com.employee.EmployeeProfileService.client;

import com.employee.EmployeeProfileService.dto.request.NotificationPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "NOTIFICATION-SERVICE", url = "http://localhost:7075")
public interface NotificationClient {

    @PostMapping("/api/notifications/internal/send")
    void sendPrivateNotification(@RequestBody NotificationPayload payload);
}
