package com.employee.AuthService.exception;

import com.employee.AuthService.dto.response.SingleResponse;
import com.employee.AuthService.enums.CustomStatus;
import org.springframework.dao.DataIntegrityViolationException;
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
    public ResponseEntity<SingleResponse<?>> handleCustomException(CustomException e){
        SingleResponse<?> response = new SingleResponse<>(null,e.getCustomStatus());
        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<SingleResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        // Default fallback status wrapper definitions
        CustomStatus status = CustomStatus.FAILURE;
        int httpStatusCode = org.springframework.http.HttpStatus.CONFLICT.value(); // HTTP 409

        // Optional: Parse the explicit constraint name text for ultra-precise UI handling
//        String rootMessage = e.getRootCause() != null ? e.getRootCause().getMessage() : e.getMessage();
//        if (rootMessage != null && rootMessage.contains("ukpho8r9u400rhedgim34cd0lbc")) {
//            // You can attach a specific targeted custom error text response wrapper
//            return ResponseEntity
//                    .status(httpStatusCode)
//                    .body(new SingleResponse<>("This email address has an active verification profile initialization setup pending.", status, httpStatusCode));
//        }

        SingleResponse<?> response = new SingleResponse<>(null, status, httpStatusCode);
        return ResponseEntity.status(httpStatusCode).body(response);
    }
}
