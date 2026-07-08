package com.employee.NotificationService.controller;

import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.service.CommunicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communication/email")
@RequiredArgsConstructor
public class CommunicationInternalController {

    private final CommunicationService communicationService;

    public ResponseEntity<String> sendLeaveEmail(@RequestBody LeaveEmailRequest request){
        communicationService.sendLeaveEmail(request);
        return ResponseEntity.ok("Leave Email Process");
    }
}
