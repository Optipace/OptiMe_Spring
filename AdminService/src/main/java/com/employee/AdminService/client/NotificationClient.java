package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.NotificationPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

//@FeignClient(name = "NOTIFICATION-SERVICE", url = "http://localhost:7075")
@FeignClient(name = "NOTIFICATION-SERVICE")
public interface NotificationClient {

    @PostMapping("/api/notifications/internal/broadcast")
    void sendBroadCastNotification(@RequestBody NotificationPayload payload);
}
