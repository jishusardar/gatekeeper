package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.projects.gatekeeper.api_management_service.Entity.ManagementUser;

@Getter
@AllArgsConstructor
public class TenantAdminResponse {
    private Long id;
    private Long organizationId;
    private String username;
    private boolean enabled;
    public static TenantAdminResponse from(ManagementUser managementUser){
        return new TenantAdminResponse(managementUser.getId(),managementUser.getOrganization().getId(),
                managementUser.getUsername(),managementUser.isEnabled());
    }
}
