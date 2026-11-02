package me.projects.gatekeeper.api_management_service.Exception;

import me.projects.gatekeeper.api_management_service.dto.Response.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleAPIException(ApiException apiException){
        return response(apiException.getStatus(),apiException.getMessage(),null);
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception){
        Map<String,String> errors=new LinkedHashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(error->
                errors.put(error.getField(),error.getDefaultMessage()));
        return response(HttpStatus.BAD_REQUEST,"Request Validation Failed",errors);
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception exception){
        return response(HttpStatus.INTERNAL_SERVER_ERROR
                ,"Unexpected Server Error", null);
    }
    private ResponseEntity<ErrorResponse> response(HttpStatus httpStatus,String Message,
                                                   Map<String,String> errors){
        return ResponseEntity.status(httpStatus).body(new ErrorResponse(httpStatus.value()
                ,Message,errors, LocalDateTime.now()));
    }
}
