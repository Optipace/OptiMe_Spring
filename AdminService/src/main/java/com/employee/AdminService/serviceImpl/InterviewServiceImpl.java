package com.employee.AdminService.serviceImpl;

import com.employee.AdminService.client.NotificationClient;
import com.employee.AdminService.dto.request.InterviewPayload;
import com.employee.AdminService.dto.request.InterviewRequest;
import com.employee.AdminService.dto.response.SingleResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class InterviewServiceImpl implements InterviewService {

    private final InterviewUtil interviewUtil;
    private final NotificationClient notificationClient;
    private final TokenRepository tokenRepository;
    private final ApplicantDetailsRepository applicantDetailsRepository;
    private final String url = "https://www.optipace.in/register?token=";

    @Override
    @Transactional
    public SingleResponse<String> generateToken(InterviewRequest request) {
        // 1. Generate new JWT
        String jwtToken = interviewUtil.generateToken(request.getEmailId(), request.getRole());

        // 2. "Upsert" Logic: Find existing by email, or create new if none exists
        TokenDetails tokenDetails = tokenRepository.findByEmailId(request.getEmailId())
                .orElse(new TokenDetails());

        // 3. Update the fields.
        // If it's new, Hibernate sets BOTH createdOn and updatedOn.
        // If it exists, Hibernate leaves createdOn alone and ONLY bumps updatedOn.
        tokenDetails.setToken(jwtToken);
        tokenDetails.setEmailId(request.getEmailId());
        tokenDetails.setAvailable(AvailableEnum.Y);

        tokenRepository.save(tokenDetails);

        // 4. Send Email
        InterviewPayload payload = new InterviewPayload(request.getEmailId(), jwtToken, url + jwtToken);
        try {
            notificationClient.sendInterviewEmail(payload);
            log.info("Triggered interview email for {}", request.getEmailId());
        } catch (FeignException e) {
            throw new CustomException("Failed to send email", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new SingleResponse<>(null, CustomStatus.SUCCESS);
    }

//    public SingleResponse<Boolean> validateToken(String tokenStr) {
//        TokenDetails tokenDetails = getValidTokenFromDb(tokenStr);
//        return new SingleResponse<>(true, CustomStatus.SUCCESS);
//    }

    @Override
    @Transactional
    public SingleResponse<String> submitDetails(String token, ApplicantDetails applicantDetails) {
        // 1. Validate the token dynamically (4-hour check)
        TokenDetails tokenDetails = getValidTokenFromDb(token);

        // 2. Extract email from JWT to ensure they aren't submitting for someone else
        String jwtEmail = interviewUtil.extractEmailId(token);
        if (!jwtEmail.equalsIgnoreCase(applicantDetails.getGmailId())) {
            throw new CustomException("Email ID does not match the token", HttpStatus.BAD_REQUEST);
        }

        // 3. Save the applicant details to db
        applicantDetailsRepository.save(applicantDetails);

        // 4. Delete the token so it can never be used again
        tokenRepository.delete(tokenDetails);
        log.info("Token successfully utilized and deleted for candidate: {}", jwtEmail);

        return new SingleResponse<>("Application submitted successfully", CustomStatus.SUCCESS);
    }

    // --- HELPER: DYNAMIC 4-HOUR EXPIRATION LOGIC ---
    // --- HELPER: DYNAMIC 4-HOUR EXPIRATION LOGIC ---
    private TokenDetails getValidTokenFromDb(String tokenStr) {
        TokenDetails tokenDetails = tokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new CustomException("Invalid Token", HttpStatus.UNAUTHORIZED));

        if (tokenDetails.getAvailable() != AvailableEnum.Y) {
            throw new CustomException("Token has already been used or invalidated", HttpStatus.UNAUTHORIZED);
        }

        // CRITICAL FIX: Calculate 4 hours dynamically using getUpdatedOn()
        LocalDateTime expirationTime = tokenDetails.getUpdatedOn().plusHours(4);

        if (LocalDateTime.now().isAfter(expirationTime)) {
            tokenDetails.setAvailable(AvailableEnum.N);
            tokenRepository.save(tokenDetails);
            throw new CustomException("Token has expired. Please request a new link.", HttpStatus.UNAUTHORIZED);
        }

        // Verify JWT signature
        try {
            interviewUtil.extractClaims(tokenStr);
        } catch (Exception e) {
            throw new CustomException("Invalid Token Signature", HttpStatus.UNAUTHORIZED);
        }

        return tokenDetails;
    }
}