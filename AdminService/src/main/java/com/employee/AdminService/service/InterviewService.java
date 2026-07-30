package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.model.ApplicantDetails;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;

public interface InterviewService {
    public SingleResponse<String> generateToken(InterviewRequest request);

    public SingleResponse<String> submitDetails(String token, ApplicantDetails applicantDetails);

}
