package com.employee.AuthService.repository;

import com.employee.AuthService.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmailId(String emailId);

    Optional<User> findByUserName(String userName);

    Optional<User> findByContact(String contact);

    Optional<User> findByEmployeeId(String employeeId);

    @Query("SELECT u FROM User u WHERE u.emailId = :emailId OR u.contact = :contact")
    Optional<User> findByEmailIdOrContact(@Param("emailId") String emailId,@Param("contact") String contact);

    Optional<User> findByEmailIdAndContact(String emailId, String contact);
}
