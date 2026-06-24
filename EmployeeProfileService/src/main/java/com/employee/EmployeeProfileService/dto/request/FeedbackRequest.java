package com.employee.EmployeeProfileService.dto.request;

import com.employee.EmployeeProfileService.enums.FeedbackEnum;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackRequest {
    private String feedback;
    private FeedbackEnum feedbackStatus;
}
