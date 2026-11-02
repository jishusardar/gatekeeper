package me.projects.gatekeeper.api_management_service.dto.Request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;

@Getter
@Setter
public class OrganizationStatusRequest {
    @NotNull
    private OrganizationStatus status;
}
