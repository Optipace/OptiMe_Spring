package com.employee.NotificationService.controller;

import com.employee.NotificationService.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/notifications/internal")
@RequiredArgsConstructor
public class NotificationInternalController {
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send")
    public ResponseEntity<?> sendPrivateNotification(@RequestBody NotificationRequest request){
        String uniqueChannel = "/queue/notifications-"+request.getEmployeeId();

        messagingTemplate.convertAndSend(uniqueChannel,request);

        return ResponseEntity.ok("Notification forwarded successfully");
    }

//    @PostMapping("/send/toAll")
//    public ResponseEntity<?> sendNotificationToAll(@RequestBody NotificationRequest request);
}
