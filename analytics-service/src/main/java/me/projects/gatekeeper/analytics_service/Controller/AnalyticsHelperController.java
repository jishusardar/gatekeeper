package me.projects.gatekeeper.analytics_service.Controller;

import me.projects.gatekeeper.analytics_service.Dto.AnalyticHelperDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
public class AnalyticsHelperController {
    private String serviceName;
    private String serviceDescription;
    public AnalyticsHelperController(@Value("${info.app.name}") String serviceName, @Value("${info.app.description}") String serviceDescription){
        this.serviceDescription=serviceDescription;
        this.serviceName=serviceName;
    }
    @GetMapping
    public ResponseEntity<AnalyticHelperDto> getHelp(){
        return ResponseEntity.ok(new AnalyticHelperDto(serviceName,serviceDescription));
    }
}
