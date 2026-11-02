package me.projects.gatekeeper.api_management_service.Controller;

import me.projects.gatekeeper.api_management_service.dto.apiManagementHealthDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class ApimanagementHelperController{
    private String serviceName;
    private String serviceDescription;
    public ApimanagementHelperController(@Value("${info.app.name}") String serviceName , @Value("${info.app.description}") String serviceDescription){
        this.serviceDescription=serviceDescription;
        this.serviceName=serviceName;
    }
    @GetMapping
    public ResponseEntity<apiManagementHealthDto> getHealth(){
        return ResponseEntity.ok(new apiManagementHealthDto(serviceName,serviceDescription));
    }

}