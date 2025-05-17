package com.vishal.electronicsstore.exception;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.vishal.electronicsstore.dto.ApiResponseMessage;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponseMessage> handleBadCredentials(BadCredentialsException e) {
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .success(false)
                .build();
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseMessage> resourceNotFoundExceptionHandler(ResourceNotFoundException e) {
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.NOT_FOUND)
                .success(false)
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BadAPIRequestException.class)
    public ResponseEntity<ApiResponseMessage> badAPIRequestExceptionHandler(BadAPIRequestException e) {
        ApiResponseMessage response = ApiResponseMessage.builder()
                .message(e.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .success(false)
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> methodArgumentNotValidExceptionHandler(
            MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        Map<String, Object> response = new HashMap<>();
        allErrors.stream().forEach(objectError -> {
            String field = ((FieldError) objectError).getField();
            String message = objectError.getDefaultMessage();
            response.put(field, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

}
