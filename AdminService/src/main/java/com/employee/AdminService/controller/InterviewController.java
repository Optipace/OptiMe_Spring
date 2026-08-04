package com.employee.AdminService.controller;

import com.employee.AdminService.dto.request.ApplicantDetailsRequest;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.PageResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.dto.response.SubmittedApplicationResponse;
import com.employee.AdminService.dto.response.UnSubmittedResponse;
import com.employee.AdminService.service.InterviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    public ResponseEntity<SingleResponse<String>> submitDetails(@RequestParam("token")String token, @Valid @RequestBody ApplicantDetailsRequest request){
        SingleResponse<String> response = interviewService.submitDetails(token, request);
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/allUnsubmittedDetails")
    public ResponseEntity<SingleResponse<List<UnSubmittedResponse>>> unSubmittedDetails(){
        SingleResponse<List<UnSubmittedResponse>> response = interviewService.unSubmittedDetails();
        return ResponseEntity.status(200).body(response);
    }

    @GetMapping("/submittedDetails")
    public ResponseEntity<SingleResponse<PageResponse<SubmittedApplicationResponse>>> getAllSubmittedDetails(@RequestParam(defaultValue = "0")int page, @RequestParam(defaultValue = "5")int size){
        Pageable pageable = PageRequest.of(page,size);
        SingleResponse<PageResponse<SubmittedApplicationResponse>> response = interviewService.getAllSubmittedDetails(pageable);
        return ResponseEntity.status(200).body(response);
    }

    @DeleteMapping("/deleteApplication")
    public ResponseEntity<SingleResponse<String>> deleteApplicationById(@Valid @RequestParam Long id){
        SingleResponse<String> response = interviewService.deleteApplicationById(id);
        return ResponseEntity.status(200).body(response);
    }
}
