package com.employee.AuthService.repository;

import com.employee.AuthService.model.UserOtp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOtpRepository extends JpaRepository<UserOtp, Long> {

//    Optional<UserOtp> findByIdentifier(String identifier);

    Optional<UserOtp> findByEmailIdAndContact(String emailId, String contact);

    Optional<UserOtp> findByEmailIdOrContact(String emailId, String contact);

    Optional<UserOtp> findByValidationToken(String validationToken);

}
