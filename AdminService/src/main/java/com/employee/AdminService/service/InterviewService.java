package com.employee.AdminService.service;

import com.employee.AdminService.dto.request.ApplicantDetailsRequest;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.PageResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.dto.response.SubmittedApplicationResponse;
import com.employee.AdminService.dto.response.UnSubmittedResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface InterviewService {
    SingleResponse<String> generateToken(InterviewRequest request);

    SingleResponse<String> submitDetails(String token, ApplicantDetailsRequest request);

    SingleResponse<List<UnSubmittedResponse>> unSubmittedDetails();

    SingleResponse<PageResponse<SubmittedApplicationResponse>> getAllSubmittedDetails(Pageable pageable);

    SingleResponse<String> deleteApplicationById(Long id);

}
