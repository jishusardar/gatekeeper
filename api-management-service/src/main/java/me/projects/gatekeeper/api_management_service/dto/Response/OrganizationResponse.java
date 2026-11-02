package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class OrganizationResponse {
    private Long id;
    private String name;
    private String slug;
    private OrganizationStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static OrganizationResponse from (Organization organization){
        return new OrganizationResponse(organization.getId(),organization.getName(),organization.getSlug(),organization.getStatus(),organization.getCreated_at(),organization.getUpdated_at());
    }
}
