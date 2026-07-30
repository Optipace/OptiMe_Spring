package com.employee.AdminService.repository;

import com.employee.AdminService.enums.AvailableEnum;
import com.employee.AdminService.model.TokenDetails;
import org.antlr.v4.runtime.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<TokenDetails, Long> {
    Optional<TokenDetails> findByEmailId(String emailId);
    Optional<TokenDetails> findByToken(String token);
    Optional<TokenDetails> findByEmailIdAndAvailable(String emailId, AvailableEnum available);
}
