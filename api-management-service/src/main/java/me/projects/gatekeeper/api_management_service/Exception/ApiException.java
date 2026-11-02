package me.projects.gatekeeper.api_management_service.Exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {
    private final HttpStatus status;
    public ApiException(HttpStatus status,String Message){
        super(Message);
        this.status=status;
    }
    public HttpStatus getStatus(){
        return status;
    }
}
