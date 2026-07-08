package com.employee.NotificationService.serviceImpl;

import com.employee.NotificationService.dto.request.LeaveEmailRequest;
import com.employee.NotificationService.service.CommunicationService;
import com.employee.NotificationService.service.EmailService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;

@Service
@AllArgsConstructor
public class CommunicationServiceImpl implements CommunicationService {

    private final EmailService emailService;

    @Override
    public void sendLeaveEmail(LeaveEmailRequest request) {
        Context context = new Context();
//        TODO : Must be completed
    }
}
