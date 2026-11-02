package me.projects.gatekeeper.api_management_service.Controller;

import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Service.RouteService;
import me.projects.gatekeeper.api_management_service.dto.Request.AuditRecordResponse;
import me.projects.gatekeeper.api_management_service.dto.Request.RouteRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.RouteStatusRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteResponse;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteVersionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tenant")
@RequiredArgsConstructor
public class TenantRouteController {
    private final RouteService routeService;
    @PostMapping("/routes")
    public ResponseEntity<RouteResponse> create(@Valid @RequestBody RouteRequest routeRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(routeService.create(routeRequest));
    }
    @GetMapping("/routes")
    public ResponseEntity<List<RouteResponse>> findAll(){
        return ResponseEntity.ok(routeService.findAllForTenant());
    }
    @GetMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(routeService.findById(id));
    }
    @PutMapping("/routes/{id}")
    public ResponseEntity<RouteResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody RouteRequest routeRequest){
        return ResponseEntity.ok(routeService.update(id,routeRequest));
    }
    @PatchMapping("/routes/{id}/status")
    public ResponseEntity<RouteResponse> changeStatus(@PathVariable Long id,
                                                      @RequestBody RouteStatusRequest routeStatusRequest){
        return ResponseEntity.ok(routeService.changeStatus(id,routeStatusRequest));
    }
    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/routes/{routeId}")
    public ResponseEntity<List<RouteVersionResponse>> getVersions(@PathVariable String routeId){
        return ResponseEntity.ok(routeService.getVersions(routeId));
    }
    @GetMapping("/audit-records")
    public ResponseEntity<List<AuditRecordResponse>> getAuditRecords(){
        return ResponseEntity.ok(routeService.getAllRecords());
    }
}
