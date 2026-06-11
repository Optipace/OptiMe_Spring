package com.employee.AttendanceService.exception;

import com.Employee.dto.response.ApiResponse;
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
        ApiResponse<?> response = new ApiResponse<>(false, ex.getBindingResult().getFieldError().getDefaultMessage(), null, LocalDateTime.now(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<?>>handleCustomException(CustomException ex){
        ApiResponse<?> response = new ApiResponse<>(false, ex.getMessage(), null, LocalDateTime.now(), ex.getHttpStatus());
        return new ResponseEntity<>(response, ex.getHttpStatus());
    }
}
