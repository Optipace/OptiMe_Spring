package com.employee.EmployeeProfileService.model;

import com.employee.EmployeeProfileService.enums.FeedbackStatusEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "feedback")
public class Feedback {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String feedback;

    @Column(nullable = true)
    private String employeeName;

    @Enumerated(EnumType.STRING)
    private FeedbackStatusEnum statusEnum;

}
