package com.employee.AttendanceService.exception;

import com.employee.AttendanceService.enums.CustomStatus;
import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {

    private final CustomStatus customStatus;
    private final int statusCode;

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.customStatus = null;
        this.statusCode = httpStatus.value();
    }
    public CustomException(String message, CustomStatus customStatus, int statusCode) {
        super(message); // message can be null here safely
        this.customStatus = customStatus;
        this.statusCode = statusCode;
    }

    // Getters so your Exception Handler can read these values
    public CustomStatus getCustomStatus() {
        return customStatus;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
