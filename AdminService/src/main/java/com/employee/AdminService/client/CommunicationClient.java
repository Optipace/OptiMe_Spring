package com.employee.AdminService.client;

import com.employee.AdminService.dto.request.InterviewPayload;
import com.employee.AdminService.dto.request.NotificationPayload;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "COMMUNICATION-SERVICE", url = "http://localhost:7075")
@FeignClient(name = "COMMUNICATION-SERVICE")
public interface CommunicationClient {

    @PostMapping("/api/notifications/internal/broadcast")
    void sendBroadCastNotification(@RequestBody NotificationPayload payload);

    @PostMapping("/api/communication/email/sendWelcomeEmail")
    void sendAccountCreatedEmail(@RequestParam("emailId") String emailId, @RequestParam("contact") String contact);

    @PostMapping("/api/communication/email/interviewEmail")
    void sendInterviewEmail(@RequestBody InterviewPayload payload);

    @PostMapping("/api/notifications/internal/send")
    void sendPrivateNotification(@RequestBody NotificationPayload payload);
}
