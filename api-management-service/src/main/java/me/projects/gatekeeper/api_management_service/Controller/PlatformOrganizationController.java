package me.projects.gatekeeper.api_management_service.Controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Service.OrganizationService;
import me.projects.gatekeeper.api_management_service.dto.Response.OrganizationResponse;
import me.projects.gatekeeper.api_management_service.dto.Request.OrganizationRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.OrganizationStatusRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.TenantAdminRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.TenantAdminResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/platform/organizations")
@RequiredArgsConstructor
public class PlatformOrganizationController {
    private final OrganizationService organizationService;
    @PostMapping
    public ResponseEntity<OrganizationResponse> create(@Valid @RequestBody OrganizationRequest organizationRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.create(organizationRequest));
    }
    @GetMapping
    public ResponseEntity<List<OrganizationResponse>> findALL(){
        return ResponseEntity.ok(organizationService.findAll());
    }
    @GetMapping("/{id}")
    public ResponseEntity<OrganizationResponse> findById(@PathVariable Long id){
        return ResponseEntity.ok(organizationService.findById(id));
    }
    @PutMapping("/{id}")
    public ResponseEntity<OrganizationResponse> UpdateById(@PathVariable Long id,@Valid @RequestBody OrganizationRequest organizationRequest){
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(organizationService.updateById(id,organizationRequest));
    }
    @PatchMapping("/{id}/status")
    public ResponseEntity<OrganizationResponse> ChangeStatus(@PathVariable Long id,@Valid @RequestBody OrganizationStatusRequest organizationStatusRequest){
        return ResponseEntity.status(HttpStatus.OK).body(organizationService.changeStatus(id,organizationStatusRequest));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        organizationService.delete(id);
        return ResponseEntity.noContent().build();
    }
    //CRUD OPERATION FOR TENANT ADMIN
    @PostMapping("/{org_id}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> createTenantAdmin(@PathVariable Long org_id,@Valid @RequestBody TenantAdminRequest tenantAdminRequest){
        return ResponseEntity.status(HttpStatus.CREATED).body(organizationService.createTenantAdmin(org_id,tenantAdminRequest));
    }
    @GetMapping("/{org_id}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> getTenantAdmin(@PathVariable Long org_id){
        return ResponseEntity.ok(organizationService.getTenantAdmin(org_id));
    }
    @PutMapping("/{org_id}/tenant-admin")
    public ResponseEntity<TenantAdminResponse> updateTenantAdmin(@PathVariable Long org_id,@Valid @RequestBody TenantAdminRequest tenantAdminRequest){
        return ResponseEntity.ok(organizationService.UpdateTenantAdmin(org_id,tenantAdminRequest));
    }
    @DeleteMapping("/{org_id}/tenant-admin")
    public ResponseEntity<Void> deleteTenantAdmin(@PathVariable Long org_id){
        organizationService.deleteTenantAdmin(org_id);
        return ResponseEntity.noContent().build();
    }
}
