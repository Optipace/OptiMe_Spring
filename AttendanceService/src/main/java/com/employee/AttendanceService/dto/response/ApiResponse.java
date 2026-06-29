package com.employee.AttendanceService.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private T data;
    private int statusCode;

    public ApiResponse(String message, T data, HttpStatus httpStatus) {
        this.message = message;
        this.data = data;
        this.statusCode = httpStatus.value(); // Extracts the integer code (e.g., 400, 200)
    }

    // Getter for status
    @JsonIgnore
    public int getStatus() {
        return statusCode;
    }
}
