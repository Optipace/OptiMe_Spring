package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.CommunicationClient;
import com.employee.AdminService.dto.request.ApplicantDetailsRequest;
import com.employee.AdminService.dto.request.InterviewPayload;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.PageResponse;
import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.dto.response.SubmittedApplicationResponse;
import com.employee.AdminService.dto.response.UnSubmittedResponse;
import com.employee.AdminService.enums.ApplicationStatus;
import com.employee.AdminService.enums.AvailableEnum;
import com.employee.AdminService.enums.CustomStatus;
import com.employee.AdminService.exception.CustomException;
import com.employee.AdminService.model.ApplicantDetails;
import com.employee.AdminService.model.TokenDetails;
import com.employee.AdminService.repository.ApplicantDetailsRepository;
import com.employee.AdminService.repository.TokenRepository;
import com.employee.AdminService.service.InterviewService;
import com.employee.AdminService.util.InterviewUtil;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@RequiredArgsConstructor
@Service
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewUtil interviewUtil;
    private final CommunicationClient communicationClient;
    private final TokenRepository tokenRepository;
    private final ApplicantDetailsRepository applicantDetailsRepository;
    private final ModelMapper modelMapper;
    private final String url = "https://www.optipace.in/submit?token=";

    @Override
    @Transactional
    public SingleResponse<String> generateToken(InterviewRequest request) {
        // 1. Generate new JWT
        String token = interviewUtil.generateToken(request.getEmailId(), request.getRole());

        // 2. "Upsert" Logic: Find existing by email, or create new if none exists
        TokenDetails tokenDetails = tokenRepository.findByEmailId(request.getEmailId())
                .orElse(new TokenDetails());

        // 3. Update the fields.
        // If it's new, Hibernate sets BOTH createdOn and updatedOn.
        // If it exists, Hibernate leaves createdOn alone and ONLY bumps updatedOn.
        tokenDetails.setToken(token);
        tokenDetails.setEmailId(request.getEmailId());
        tokenDetails.setAvailable(AvailableEnum.Y);

        tokenRepository.save(tokenDetails);

        // 4. Send Email
        InterviewPayload payload = new InterviewPayload(request.getEmailId(), token, url + token);
        try {
            communicationClient.sendInterviewEmail(payload);
            log.info("Triggered interview email for {}", request.getEmailId());
        } catch (FeignException e) {
            throw new CustomException(null, CustomStatus.EMAIL_DELIVERY_FAILED, 409);
        }

        return new SingleResponse<>(token, CustomStatus.SUCCESS);
    }

//    public SingleResponse<Boolean> validateToken(String tokenStr) {
//        TokenDetails tokenDetails = getValidTokenFromDb(tokenStr);
//        return new SingleResponse<>(true, CustomStatus.SUCCESS);
//    }

    @Override
    @Transactional
    public SingleResponse<String> submitDetails(String token, ApplicantDetailsRequest request) {
        // 1. Validate the token dynamically (4-hour check)
        TokenDetails tokenDetails = getValidTokenFromDb(token);

        // 2. Extract email from JWT to ensure they aren't submitting for someone else
        String jwtEmail = interviewUtil.extractEmailId(token);
        if (!jwtEmail.equalsIgnoreCase(request.getGmailId())) {
            throw new CustomException(null, CustomStatus.INVALID_EMAIL_ADDRESS, 409);
        }

        ApplicantDetails applicantDetails = new ApplicantDetails();
        applicantDetails.setCandidateFirstName(request.getCandidateFirstName());
        applicantDetails.setCandidateMiddleName(request.getCandidateMiddleName());
        applicantDetails.setCandidateLastName(request.getCandidateLastName());
        applicantDetails.setFatherName(request.getFatherName());
        applicantDetails.setMotherName(request.getMotherName());
        applicantDetails.setMaritalStatus(request.getMaritalStatus());
        applicantDetails.setMobileNumber(request.getMobileNumber());
        applicantDetails.setGmailId(request.getGmailId());
        applicantDetails.setCurrentWorkStatus(request.getCurrentWorkStatus());
        applicantDetails.setCurrentLastCtc(request.getCurrentLastCtc());
        applicantDetails.setExpectation(request.getExpectation());
        applicantDetails.setNoticePeriod(request.getNoticePeriod());
        applicantDetails.setRelocation(request.getRelocation());
        applicantDetails.setVirtualInterviewStatus(request.getVirtualInterviewStatus());
        applicantDetails.setStatus(ApplicationStatus.COMPLETED);

        // 3. Save the applicant details to db
        applicantDetailsRepository.save(applicantDetails);

        // 4. Delete the token so it can never be used again
        tokenRepository.delete(tokenDetails);
        log.info("Token successfully utilized and deleted for candidate: {}", jwtEmail);

        return new SingleResponse<>("Application submitted successfully", CustomStatus.SUCCESS);
    }

    @Override
    public SingleResponse<List<UnSubmittedResponse>> unSubmittedDetails() {
        List<TokenDetails> tokenDetails = tokenRepository.findAll();
        List<UnSubmittedResponse> response = tokenDetails.stream()
                .map(token -> {
                    UnSubmittedResponse response1 = new UnSubmittedResponse();
                    response1.setId(token.getId());
                    response1.setEmailId(token.getEmailId());
                    response1.setToken(token.getToken());

                    return response1;
                })
                .toList();

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    @Override
    public SingleResponse<PageResponse<SubmittedApplicationResponse>> getAllSubmittedDetails(Pageable pageable) {
        Page<ApplicantDetails> applicantDetailsPage = applicantDetailsRepository.findAll(pageable);
        List<ApplicantDetails> applicantDetailsList = applicantDetailsPage.getContent();

        if(applicantDetailsPage.isEmpty()){
            throw new CustomException(null, CustomStatus.NO_OFFICE_RECORDS_FOUND, 409); // TODO : Change to application details not found
        }

        List<SubmittedApplicationResponse> applicationResponseList = applicantDetailsList.stream()
                .map(applicantDetails -> modelMapper.map(applicantDetails, SubmittedApplicationResponse.class))
                .toList();

        PageResponse<SubmittedApplicationResponse> response = new PageResponse<>(
                applicationResponseList,
                applicantDetailsPage.getNumber(),
                applicantDetailsPage.getSize(),
                applicantDetailsPage.getTotalElements(),
                applicantDetailsPage.getTotalPages(),
                applicantDetailsPage.isLast()
        );

        return new SingleResponse<>(
                response,
                CustomStatus.SUCCESS
        );
    }

    // --- HELPER: DYNAMIC 4-HOUR EXPIRATION LOGIC ---
    private TokenDetails getValidTokenFromDb(String tokenStr) {
        TokenDetails tokenDetails = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new CustomException(null, CustomStatus.INVALID_VALIDATION_TOKEN, 409));

        if (tokenDetails.getAvailable() != AvailableEnum.Y) {
            throw new CustomException(null, CustomStatus.TOKEN_ALREADY_USED,409);
        }

        // CRITICAL FIX: Calculate 4 hours dynamically using getUpdatedOn()
        LocalDateTime expirationTime = tokenDetails.getUpdatedOn().plusHours(4);

        if (LocalDateTime.now().isAfter(expirationTime)) {
            tokenDetails.setAvailable(AvailableEnum.N);
            tokenRepository.save(tokenDetails);
            throw new CustomException(null, CustomStatus.INVALID_OR_EXPIRED_TOKEN,409);
        }

        // Verify JWT signature
        try {
            interviewUtil.extractClaims(tokenStr);
        } catch (Exception e) {
            throw new CustomException(null, CustomStatus.INVALID_VALIDATION_TOKEN, 409); // "Invalid Token Signature"
        }

        return tokenDetails;
    }
}