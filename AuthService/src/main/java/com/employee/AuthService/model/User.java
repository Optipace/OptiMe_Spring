package com.employee.AuthService.model;

import com.employee.AuthService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
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

    @NotBlank(message = "Email Id is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide valid Email-Id"
    )
    private String emailId;

//    @NotBlank(message = "Email Id is mandatory")
    @Size(max = 40, message = "Email must not exceed 40 characters")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}",
            message = "Provide valid Email-Id"
    )
    private String personalEmail;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit number")
    @Column(name = "contact", nullable = false, length = 10) // TODO unique constraint (unique=true) is removed need to be checked in the service layer based on role
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(length = 12)
    private UserStatusEnum userStatus;

    @Enumerated(EnumType.ORDINAL)
    @Column(nullable = false, length = 5,columnDefinition = "integer default 0")
    private IsDiscontinued isDiscontinued = IsDiscontinued.NO;

//    @Enumerated(EnumType.STRING)
//    @Column(length = 2)
//    private RegisterEnum registerStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private RoleEnum role = RoleEnum.EMP;

    @NotNull(message = "Creator information is mandatory")
    @Column(name = "created_by", nullable = false, updatable = false)
    private Long createdBy;

    @Column(name = "created_on",insertable = false, nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @org.hibernate.annotations.Generated(event = EventType.INSERT)
    private LocalDateTime createdOn;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Password password;
}