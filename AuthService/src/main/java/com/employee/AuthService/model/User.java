package com.employee.AuthService.model;

import com.employee.AuthService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_name", length = 50)
    private String userName;

    @NotBlank(message = "Employee ID cannot be blank")
    @Column(name = "employee_id", unique = true, nullable = false, length = 12)
    private String employeeId;

    @Email(message = "Please provide a valid email address")
    @Column(name = "email_id", nullable = false, unique = true, length = 50)
    private String emailId;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit number")
    @Column(name = "contact", nullable = false, unique = true, length = 10)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private UserStatusEnum userStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 2)
    private RegisterEnum registerStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private RoleEnum role = RoleEnum.EMP;

    @NotBlank(message = "Creator information is mandatory")
    @Column(name = "created_by", nullable = false, updatable = false)
    private String createdBy;

    @Column(name = "created_on",insertable = false, nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Generated(event = EventType.INSERT)
    private LocalDateTime createdOn;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Password password;
}