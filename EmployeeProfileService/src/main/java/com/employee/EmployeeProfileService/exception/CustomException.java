package com.employee.EmployeeProfileService.exception;

import org.springframework.http.HttpStatus;


public class CustomException extends RuntimeException {

	private final String errorCode;
    private final HttpStatus httpStatus;

    public CustomException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = String.valueOf(httpStatus.value()); // derive from HttpStatus
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

