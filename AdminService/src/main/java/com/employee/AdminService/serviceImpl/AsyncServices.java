package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.CommunicationClient;
import com.employee.AdminService.dto.request.NotificationPayload;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.enums.CustomStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AsyncServices {
    private final CommunicationClient communicationClient;

    @Async("mailTaskThread")
    public void AsyncAccountCreatedEmail(String emailId, String contact) {
        log.info("Triggered account created email");
        communicationClient.sendAccountCreatedEmail(emailId, contact);
        log.info("Communication service is called to send welcome email");
    }

    @Async("mailTaskThread")
    public void AsyncBroadCastNotification(String employeeName) {
        NotificationPayload payload = new NotificationPayload();
        payload.setTitle("Company Announcement");
        payload.setMessage("Please welcome our new employee: " + employeeName);
        payload.setType("INFO");

        communicationClient.sendBroadCastNotification(payload);
        log.info("Notification is broadcasted to everyone");
    }
}
