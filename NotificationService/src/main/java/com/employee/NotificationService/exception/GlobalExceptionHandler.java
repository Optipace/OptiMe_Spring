package com.employee.NotificationService.exception;

import com.employee.NotificationService.dto.response.SingleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SingleResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex){
        SingleResponse<?> response = new SingleResponse<>(ex.getBindingResult().getFieldError().getDefaultMessage(), HttpStatus.BAD_REQUEST);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SingleResponse<?>> handleCustomException(CustomException e){
        SingleResponse<?> response = new SingleResponse<>(null,e.getCustomStatus());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }
}
