package com.employee.CommunicationService.controller;

import com.employee.CommunicationService.dto.request.CheckInEventRequest;
import com.employee.CommunicationService.dto.request.NotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications/internal")
@RequiredArgsConstructor
@Slf4j
public class NotificationInternalController {
    // this talks to the open websockets
    private final SimpMessagingTemplate messagingTemplate;

    /**
     * 1-TO-1 PRIVATE MESSAGING
     * Sends a message to a specific employee (e.g., Leave Approval)
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendPrivateNotification(@RequestBody NotificationRequest request){
        log.info("Sending private notification to {}",request.getEmployeeId());
        // Unique private channel name for this specific employee
        // Example: /queue/notifications/EMP001
        String uniqueChannel = "/queue/notifications/"+request.getEmployeeId();

        messagingTemplate.convertAndSend(uniqueChannel,request);

        return ResponseEntity.ok("Notification forwarded successfully");
    }

    /**
     * 1-TO-MANY BROADCASTING
     * Sends a message to EVERYONE who is currently online and listening (e.g., New Hire)
     */
    @PostMapping("/broadcast")
    public ResponseEntity<String> sendPublicBroadcast(@RequestBody NotificationRequest request) {

        // Target: /topic/company-announcements
        // This goes to everyone!
        String broadcastChannel = "/topic/company-announcements";

        log.info("Doing company announcement");
        messagingTemplate.convertAndSend(broadcastChannel, request);

        return ResponseEntity.ok("Public broadcast sent to all employees successfully!");
    }

    /**
     * ADMIN DASHBOARD
     * Sending live check-in events to the admin dashboard
     */
    @PostMapping("/admin/liveAttendance")
    public ResponseEntity<String> broadcastLiveCheckIn(@RequestBody CheckInEventRequest request){
        // Target: /topic/admin/liveAttendance
        // Only admin dashboards subscribed to this specific topic will receive it
        String adminDashboardChannel = "/topic/admin/liveAttendance";

        log.info("Broadcasting live check-in for employee: {}({})", request.getEmployeeName(), request.getEmployeeName());
        messagingTemplate.convertAndSend(adminDashboardChannel, request);

        return ResponseEntity.ok("Check-in broadcast to admin dashboard successfully!");
    }

}
