package me.projects.gatekeeper.api_management_service.Controller;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Service.OrganizationService;
import me.projects.gatekeeper.api_management_service.dto.Request.TenantOrganizationRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.OrganizationResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tenant/organization")
@RequiredArgsConstructor
public class TenantOrganizationController {
    private final OrganizationService organizationService;

    //get
    //{
    //    "username":"foodfast-admin",
    //    "password":"foodfast-admin123"
    //}
    //{
    //    "username":"QuickShop-admin",
    //    "password":"quickshop-admin123"
    //}
    @GetMapping
    public ResponseEntity<OrganizationResponse> getOwnOrganization(){
        return ResponseEntity.ok(organizationService.getOwnOrganization());
    }
    //change
    @PutMapping
    public ResponseEntity<OrganizationResponse> updateOwnOrganization(@Valid @RequestBody TenantOrganizationRequest tenantOrganizationRequest){
        return ResponseEntity.ok(organizationService.updateOwnOrganization(tenantOrganizationRequest));
    }
}
