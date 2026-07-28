package com.employee.AttendanceService.exception;

import com.employee.AttendanceService.dto.response.ApiResponse;
import com.employee.AttendanceService.dto.response.SingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SingleResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        SingleResponse<?> response = new SingleResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SingleResponse<?>> handleCustomizedException(CustomException e){
        SingleResponse<?> response = new SingleResponse<>(null,e.getCustomStatus());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }
}
