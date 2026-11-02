package me.projects.gatekeeper.api_management_service.Event;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
@Getter
@AllArgsConstructor
public class GatewayConfigurationChangeEvent {
    private Long eventId;
    private String eventType;
    private String changeType;
    private Long OrganizationId;
    private String routeId;
    private Long configurationVersion;
    private LocalDateTime occurredAt;
}
