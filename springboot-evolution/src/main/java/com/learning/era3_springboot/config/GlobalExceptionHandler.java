package com.learning.era3_springboot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.learning.era3_springboot.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ============================================================================
 * GLOBAL EXCEPTION HANDLER
 * ============================================================================
 * 
 * This class demonstrates TWO approaches to exception handling:
 * 
 * APPROACH 1: @RestControllerAdvice with @ExceptionHandler
 *   - Simple and straightforward
 *   - Good for custom exceptions
 * 
 * APPROACH 2: Extend ResponseEntityExceptionHandler
 *   - Override built-in Spring exception handlers
 *   - More control over standard exceptions
 *   - This is what your Kotlin code was doing!
 * 
 * @ControllerAdvice vs @RestControllerAdvice:
 *   - @ControllerAdvice: For traditional MVC (can return views)
 *   - @RestControllerAdvice: For REST APIs (always returns JSON)
 *   - @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // ========================================================================
    // APPROACH 2: Override ResponseEntityExceptionHandler methods
    // This is the JAVA equivalent of your Kotlin code!
    // ========================================================================
    
    /**
     * Handle validation errors from @Valid annotation
     * 
     * This method overrides the default behavior from ResponseEntityExceptionHandler.
     * It's called automatically when @Valid fails on a request body.
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {
        
        log.error("Request Validation failed");
        
        // Convert all errors to readable strings
        List<String> validationList = ex.getBindingResult().getAllErrors()
                .stream()
                .filter(error -> error != null)
                .map(error -> {
                    if (error instanceof FieldError) {
                        FieldError fieldError = (FieldError) error;
                        return fieldError.getField() + " : " + fieldError.getDefaultMessage();
                    } else {
                        return error.getObjectName() + " : " + error.getDefaultMessage();
                    }
                })
                .collect(Collectors.toList());
        
        log.error("Validation error list : {}", validationList);
        
        int errorCode = 400;
        List<String> errorMessage;
        
        if (CollectionUtils.isEmpty(validationList)) {
            errorMessage = List.of("Error in validating fields");
        } else {
            errorMessage = validationList;
        }
        
        log.error("Service Exception | errorCode : {} | errorMessage : {}", errorCode, errorMessage);
        
        // Build JSON response
        ObjectNode responseBody = objectMapper.createObjectNode()
                .put("error", errorMessage.toString());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
    }
    
    // ========================================================================
    // APPROACH 1: Simple @ExceptionHandler methods
    // These handle exceptions NOT covered by ResponseEntityExceptionHandler
    // ========================================================================
    
    /**
     * Handle validation errors - Alternative simpler approach
     * (Commented out because we're using the override approach above)
     */
    /*
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        
        Map<String, String> errors = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ApiResponse<Map<String, String>> response = new ApiResponse<>(
                false, 
                "Validation failed", 
                errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    */
    
    /**
     * Handle IllegalArgumentException (business rule violations)
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(
            IllegalArgumentException ex) {
        log.error("IllegalArgumentException: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(ex.getMessage()));
    }
    
    /**
     * Handle all other exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unexpected error occurred", ex);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("An unexpected error occurred: " + ex.getMessage()));
    }
}

