package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.model.ApplicantDetails;
import com.employee.AdminService.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/interview")
@RequiredArgsConstructor
public class InterviewController {

    private final InterviewService interviewService;

    @PostMapping("/generateToken")
    public ResponseEntity<SingleResponse<String>> generateToken(@Valid @RequestBody InterviewRequest request){
        SingleResponse<String> response = interviewService.generateToken(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/regenerateToken")
    public ResponseEntity<SingleResponse<String>> regenerateToken(@Valid @RequestBody InterviewRequest request){
        SingleResponse<String> response = interviewService.generateToken(request);
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping("/submit")
    public ResponseEntity<SingleResponse<String>> submitDetails(@RequestParam("token")String token, @Valid @RequestBody ApplicantDetails applicantDetails){
        SingleResponse<String> response = interviewService.submitDetails(token, applicantDetails);
        return ResponseEntity.status(200).body(response);
    }
}
