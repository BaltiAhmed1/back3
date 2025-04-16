package com.plasturgie.app.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void handleGlobalException(HttpServletResponse response, Exception ex) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        
        Map<String, Object> data = new HashMap<>();
        data.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
        data.put("error", "Internal Server Error");
        data.put("message", ex.getMessage());
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(data));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", "Not Acceptable");
        body.put("message", "Requested media type is not supported. Please use application/json");
        
        return new ResponseEntity<>(body, headers, status);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public void handleBadCredentialsException(HttpServletResponse response, BadCredentialsException ex) throws IOException {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        
        Map<String, Object> data = new HashMap<>();
        data.put("status", HttpStatus.UNAUTHORIZED.value());
        data.put("error", "Unauthorized");
        data.put("message", "Invalid username or password");
        
        response.getWriter().write(new ObjectMapper().writeValueAsString(data));
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request) {
        
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", "Validation Error");
        body.put("message", "Validation failed for request");
        body.put("errors", errors);
        
        return new ResponseEntity<>(body, headers, status);
    }
}