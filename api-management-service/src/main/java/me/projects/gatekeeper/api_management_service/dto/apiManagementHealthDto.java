package me.projects.gatekeeper.api_management_service.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class apiManagementHealthDto {
    private String serviceName;
    private String serviceDescription;
}
