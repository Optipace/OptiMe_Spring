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
    // this talks to the open websockets
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 1-TO-1 PRIVATE MESSAGING
     * Sends a message to a specific employee (e.g., Leave Approval)
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendPrivateNotification(@RequestBody NotificationRequest request){
        // Unique private channel name for this specific employee
        // Example: /queue/notifications-EMP001
        String uniqueChannel = "/queue/notifications-"+request.getEmployeeId();

        messagingTemplate.convertAndSend(uniqueChannel,request);

        return ResponseEntity.ok("Notification forwarded successfully");
    }

    /**
     * 1-TO-MANY BROADCASTING
     * Sends a message to EVERYONE who is currently online and listening (e.g., New Hire)
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> sendPublicBroadcast(@RequestBody NotificationRequest request) {

        // Target: /topic/company-announcements (Notice we use /topic instead of /queue)
        // We don't append an employee ID because this goes to everyone!
        String broadcastChannel = "/topic/company-announcements";

        messagingTemplate.convertAndSend(broadcastChannel, request);

        return ResponseEntity.ok("Public broadcast sent to all employees successfully!");
    }
}
