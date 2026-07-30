package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/generateToken")
    public ResponseEntity<SingleResponse<String>> generateToken(@Valid InterviewRequest request){
        SingleResponse<String> response = interviewService.generateToken(request);
        return ResponseEntity.status(200).body(response);
    }
}
