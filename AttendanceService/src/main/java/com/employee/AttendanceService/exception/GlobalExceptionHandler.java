package com.employee.AttendanceService.exception;

import com.employee.AttendanceService.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        ApiResponse<?> response = new ApiResponse<>(ex.getBindingResult().getFieldError().getDefaultMessage(), null, HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>>handleCustomException(CustomException ex){
        ApiResponse<?> response = new ApiResponse<>(ex.getMessage(), null, ex.getHttpStatus());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }
}
