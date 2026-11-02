package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.projects.gatekeeper.api_management_service.Entity.RoutesConfigurationVersion;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class RouteVersionResponse {
    private Long id;
    private String routeId;
    private Long versionNumber;
    private String changeType;
    private String configurationSnapshot;
    private LocalDateTime createdAt;
    public static RouteVersionResponse from(RoutesConfigurationVersion configurationVersion){
        return new RouteVersionResponse(configurationVersion.getId(),configurationVersion.getRouteId(),
                configurationVersion.getVersionNumber(),configurationVersion.getChangeType(),
                configurationVersion.getConfigurationSnapshot(),configurationVersion.getCreated_at());
    }
}
