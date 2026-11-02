package me.projects.gatekeeper.api_management_service.dto.Request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TenantAdminRequest {
    @NotBlank
    @Size(max = 100)
    private String username;

    @NotBlank
    @Size(min = 8,max = 100)
    private String password;

    private boolean enabled=true;
}
