package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Map;
@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;
    private String message;
    private Map<String,String> errors;
    private LocalDateTime timestamp;
}
