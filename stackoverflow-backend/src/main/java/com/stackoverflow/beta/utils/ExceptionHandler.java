package com.stackoverflow.beta.utils;

import com.stackoverflow.beta.exception.ValidationException;
import com.stackoverflow.beta.model.CustomErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.metadata.ConstraintDescriptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ExceptionHandler {

    private ExceptionHandler() {
    }
    public static ResponseEntity<CustomErrorResponse> handleException(Exception e) {
        if (e instanceof ValidationException validationException) {
            return ResponseEntity
                    .status(validationException.getStatus())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CustomErrorResponse(validationException.getMessage(), validationException.getStatus().value()));
        }
        else if (e instanceof ConstraintViolationException constraintViolationException){
            StringBuilder exceptions = new StringBuilder();
            constraintViolationException.getConstraintViolations().forEach(
                    constraintViolation -> exceptions.append(constraintViolation.getMessage()).append(":")
            );

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new CustomErrorResponse(exceptions.substring(0, exceptions.length() - 1), HttpStatus.BAD_REQUEST.value()));
        }

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new CustomErrorResponse("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }
}