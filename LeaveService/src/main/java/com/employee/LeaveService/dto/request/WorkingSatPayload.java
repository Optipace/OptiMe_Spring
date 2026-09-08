package com.employee.LeaveService.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkingSatPayload {

    @NotEmpty(message = "Working date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private List<@NotNull(message = "Individual date cannot be null") LocalDate> workingDate;

    @NotNull(message = "Office id is required")
    private Long officeId;

    @Override
    public String toString() {
        return "WorkingSatPayload{" +
                "workingDate=" + workingDate +
                ", officeId=" + officeId +
                '}';
    }
}
