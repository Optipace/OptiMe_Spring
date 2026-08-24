package com.employee.LeaveService.exception;

import com.employee.LeaveService.dto.response.SingleResponse;
import com.employee.LeaveService.enums.CustomStatus;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDate;

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

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<SingleResponse<?>> handleConstraintViolationException(
            ConstraintViolationException ex) {

        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage
                )
                .findFirst()
                .orElse("Validation failed");

        SingleResponse<?> response = new SingleResponse<>(
                HttpStatus.BAD_REQUEST.value(),
                errorMessage
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }


    @ExceptionHandler(CustomException.class)
    public ResponseEntity<SingleResponse<?>> handleCustomException(CustomException e){
            SingleResponse<?> response = new SingleResponse<>(null, e.getCustomStatus(), e.getMessage());
            return ResponseEntity
                    .status(e.getStatusCode())
                    .body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<SingleResponse<?>> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String cleanErrorMessage = "Invalid request format.";

        // Check if the root cause is a bad date/time string format
        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException ife = (InvalidFormatException) ex.getCause();
            if (ife.getTargetType().equals(LocalDate.class)) {
                cleanErrorMessage = String.format("The provided date '%s' is invalid or does not exist on the calendar.", ife.getValue());
            }
        } else {
            cleanErrorMessage = ex.getMostSpecificCause().getMessage();
        }

        // Wrap the message using your application's custom status framework
        SingleResponse<?> errorResponse = new SingleResponse<>(
                null,
                CustomStatus.INVALID_REQUEST_FORMAT // Ensure this or a similar enum value exists
        );

        // Optional: If your SingleResponse supports setting a custom string message dynamically
        // errorResponse.setMessage(cleanErrorMessage);

        // Returning 201 as per your application's established pattern for validation failures
        return ResponseEntity.status(201).body(errorResponse);
    }
}
