package com.employee.AuthService.service.impl;

import com.employee.AuthService.client.EmployeeClient;
import com.employee.AuthService.dto.request.RefreshTokenRequest;
import com.employee.AuthService.dto.response.ApiResponse;
import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.enums.CustomStatus;
import com.employee.AuthService.exception.CustomException;
import com.employee.AuthService.model.RefreshToken;
import com.employee.AuthService.model.User;
import com.employee.AuthService.repository.RefreshTokenRepository;
import com.employee.AuthService.service.RefreshTokenService;
import com.employee.AuthService.util.JwtUtil;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@Service
public class RefreshTokenServiceImplementation implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtUtil jwtUtil;

    private final EmployeeClient employeeClient;
    @Transactional // Ensures clean dirty-checking and state synchronization
    public String create(User user, Long employeeId) {
        // Look up existing token for this user
        RefreshToken token = refreshTokenRepository.findByUser(user).orElse(null);

        if (token != null) {
            // Update the existing token row to avoid unique constraint violations
            token.setToken(UUID.randomUUID().toString());
//            token.setExpiryDate(LocalDateTime.now().plusDays(7));
        } else {
            // Create a brand new token profile
            token = new RefreshToken();
            token.setUser(user);
            token.setEmployeeId(employeeId);
            token.setToken(UUID.randomUUID().toString());
//            token.setExpiryDate(LocalDateTime.now().plusDays(7));
        }

        refreshTokenRepository.save(token);
        return token.getToken();
    }

    @Override
    public SingleResponse<?> getNewAccessToken(RefreshTokenRequest request) {
        var token = validate(request.getRefreshToken());

        String newAccessToken = jwtUtil.generateToken(
                token.getUser().getUserName(),
                token.getUser().getId().toString(),
                token.getUser().getEmailId(),
                token.getUser().getEmployeeId(),
                String.valueOf(token.getUser().getRole()),
                token.getEmployeeId().toString()
        );
        return new SingleResponse<>(
                newAccessToken,
                CustomStatus.SUCCESS
        );
    }

    // Validate refresh token
    @Transactional // Required for the .delete() execution block
    public RefreshToken validate(String token) {

        // Fetch token or throw an actionable HTTP error\
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException(null, CustomStatus.INVALID_REFRESH_TOKEN, 409));

        // Check if the token has expired
        if (refreshToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            refreshTokenRepository.delete(refreshToken); // Deletes the row securely
            throw new CustomException(null, CustomStatus.REFRESH_TOKEN_EXPIRED, 409);
        }

        return refreshToken;
    }
}
