package com.employee.NotificationService.service;

import com.employee.NotificationService.dto.request.LeaveEmailRequest;

public interface CommunicationService {
    void sendLeaveEmail(LeaveEmailRequest request);
}
