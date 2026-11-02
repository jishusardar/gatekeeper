package me.projects.gatekeeper.analytics_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
@AllArgsConstructor
public class AnalyticHelperDto {
    private String serviceName;
    private String serviceDescription;
}
