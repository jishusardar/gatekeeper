package me.projects.gatekeeper.api_management_service.Controller;

import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Service.RouteService;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping ("/api/gateway/routes")
@RequiredArgsConstructor
public class GatewayConfigurationController {
    private final RouteService routeService;
    @GetMapping
    public ResponseEntity<List<RouteResponse>> getActiveRoutes(){
        return ResponseEntity.ok(routeService.findAllForGateway());
    }
}
