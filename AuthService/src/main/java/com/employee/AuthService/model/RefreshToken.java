package com.employee.AuthService.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Generated;
import org.hibernate.generator.EventType;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 40)
    private String token;

    @Transient
    public LocalDateTime getExpiryDate() {
        if (this.createdOn == null) {
            return null;
        }
        return this.createdOn.plusDays(7);
    }

    @Column(name = "created_on", insertable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    @Generated(event = EventType.INSERT)
    private LocalDateTime createdOn;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
