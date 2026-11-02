package me.projects.gatekeeper.api_management_service.Service;

import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;
import me.projects.gatekeeper.api_management_service.Exception.ApiException;
import me.projects.gatekeeper.api_management_service.Repository.OrganizationRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TenantContext {
    private final OrganizationRepository organizationRepository;
    public Organization getOrganization(){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(!(authentication instanceof JwtAuthenticationToken)){
            throw new ApiException(HttpStatus.FORBIDDEN,"Tenant Identity is Missing");
        }
        Long OrganizationId=((JwtAuthenticationToken) authentication).getToken().getClaim("organizationId");
        if(OrganizationId==null){
            throw new ApiException(HttpStatus.FORBIDDEN,"Tenant Admin is Missing");
        }
        Organization organization=organizationRepository.findById(OrganizationId).orElseThrow(()-> new ApiException(HttpStatus.FORBIDDEN,"Organization Does Not Exist"));
        if(organization.getStatus()!= OrganizationStatus.ACTIVE){
            throw new ApiException(HttpStatus.FORBIDDEN,"Organization is Not Active");
        }
        return organization;
    }
}
