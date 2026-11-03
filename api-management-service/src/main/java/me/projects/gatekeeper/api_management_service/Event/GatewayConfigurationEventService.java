package me.projects.gatekeeper.api_management_service.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatewayConfigurationEventService {
    private final ApplicationEventPublisher eventPublisher;

    public void publish(String changeType,Long organizationId,String routeId,Long configurationVersion){
        eventPublisher.publishEvent(new GatewayConfigurationChangeEvent(UUID.randomUUID().toString(),
                "GATEWAY_CONFIGURATION_CHANGE",changeType,organizationId,routeId,
                configurationVersion, LocalDateTime.now()));
    }
}
