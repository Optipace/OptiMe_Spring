package com.employee.AdminService.exception;

import com.employee.AdminService.dto.response.SingleResponse;
import com.employee.AdminService.enums.CustomStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<SingleResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
        SingleResponse<?> response = new SingleResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    //    @ExceptionHandler(CustomException.class)
//    public ResponseEntity<SingleResponse<?>> handleCustomException(CustomException e){
//        SingleResponse<?> response = new SingleResponse<>(null,e.getCustomStatus());
//        return ResponseEntity
//                .status(e.getStatusCode())
//                .body(response);
//    }
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SingleResponse<?>> handleCustomException(CustomException e) {
        SingleResponse<?> response;

        // Check if custom status exists to prevent NullPointerException
        if (e.getCustomStatus() != null) {
            response = new SingleResponse<>(null, e.getCustomStatus(), e.getStatusCode());
        } else {
            // Fallback for CustomException instances initialized with just message and HttpStatus
            response = new SingleResponse<>(e.getMessage(), org.springframework.http.HttpStatus.valueOf(e.getStatusCode()));
        }

        return ResponseEntity
                .status(e.getStatusCode())
                .body(response);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<SingleResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        // 1. Fallback default message
        String databaseErrorMessage = "Data value exceeds permitted column length constraints.";

        // 2. Extract the exact Postgres error message safely
        if (ex.getRootCause() != null) {
            databaseErrorMessage = ex.getRootCause().getMessage();
            // This extracts: "ERROR: value too long for type character varying(13)"
        }

        // 3. Build the response (If your SingleResponse constructor supports a custom message)
        SingleResponse<?> response = new SingleResponse<>(
                null,
                CustomStatus.FAILURE, // Ensure your enum has a dedicated error tag
                databaseErrorMessage              // Pass the extracted error message string here
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST) // Return 400 Bad Request
                .body(response);

    }
}
