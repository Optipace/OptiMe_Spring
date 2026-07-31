package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.ApplicantDetailsRequest;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;

public interface InterviewService {
    public SingleResponse<String> generateToken(InterviewRequest request);

    public SingleResponse<String> submitDetails(String token, ApplicantDetailsRequest request);

}
