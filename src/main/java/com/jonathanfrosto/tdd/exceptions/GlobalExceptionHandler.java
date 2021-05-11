package com.jonathanfrosto.tdd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, List<ApiError>> beanValidation(MethodArgumentNotValidException e) {
        List<ApiError> apiErrors = e.getBindingResult().getFieldErrors()
                .stream().map(erro -> new ApiError(erro.getField(), erro.getDefaultMessage()))
                .collect(Collectors.toList());

        Map<String, List<ApiError>> errors = new HashMap<>();
        errors.put("errors", apiErrors);
        return errors;
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, List<ApiError>>> businessValidation(BusinessException e) {
        List<ApiError> apiErrors = Collections.singletonList(new ApiError(e.getMessage()));

        Map<String, List<ApiError>> errors = new HashMap<>();
        errors.put("errors", apiErrors);

        return new ResponseEntity<>(errors, HttpStatus.valueOf(e.getStatusCode()));
    }

}
