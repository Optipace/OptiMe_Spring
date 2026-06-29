package com.employee.AuthService.model;

import com.employee.AuthService.enums.RegisterEnum;
import com.employee.AuthService.enums.StatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_otp")
public class UserOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email(message = "Please provide a valid email address")
    @Column(name = "email_id", nullable = false, unique = true, length = 50)
    private String emailId;

    @Column(name = "email_otp", length = 6)
    private String emailOtp;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Contact must be a valid 10-digit number")
    @Column(nullable = false, unique = true, length = 10)
    private String contact;

    @Column(name = "mobile_otp", length = 6)
    private String mobileOtp;

    @Column(name = "created_on",insertable = false,columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP", updatable = false)
    @Generated(event = EventType.INSERT)
    private LocalDateTime createdOn;

//    @Transient
//    public LocalDateTime getExpiryTime() {
////        if (this.createdOn == null) {
////            return null;
////        }
//        return this.createdOn.plusMinutes(5);
//    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 2)
    @NotNull
    private RegisterEnum available;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    @NotNull
    private StatusEnum validated = StatusEnum.F;

    @Column(unique = true)
    private String validationToken;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    @NotNull
    private RegisterEnum registerStatus = RegisterEnum.N;

}
