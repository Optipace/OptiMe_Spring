package com.employee.EmployeeProfileService.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timeStamp;
    private int statusCode;

    public ApiResponse(boolean success, String message, T data, LocalDateTime timeStamp, HttpStatus httpStatus) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.timeStamp = timeStamp;
        this.statusCode = httpStatus.value(); // Extracts the integer code (e.g., 400, 200)
    }

    // Getter for status
    @JsonIgnore
    public int getStatus() {
        return statusCode;
    }
}
