package com.employee.AuthService.model;

import com.employee.AuthService.enums.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String userName;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String employeeId;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String emailId;

    @Column(unique = true, nullable = false)
    @NotBlank
    private String contact;

    @Enumerated(EnumType.STRING)
    private UserStatusEnum userStatus;

    @Enumerated(EnumType.STRING)
    private RegisterEnum registerStatus;

    @Enumerated(EnumType.STRING)
    private RoleEnum role = RoleEnum.EMP;

    @CreationTimestamp
    @Column(name = "created_on",updatable = false)
    private LocalDateTime createdOn;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Password password;

//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
//    private Employee employee;
}
