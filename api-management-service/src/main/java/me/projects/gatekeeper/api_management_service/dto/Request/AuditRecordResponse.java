package me.projects.gatekeeper.api_management_service.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import me.projects.gatekeeper.api_management_service.Entity.ControlPlaneAuditRecord;

import java.time.LocalDateTime;
@Setter
@AllArgsConstructor
@Getter
public class AuditRecordResponse {
    private Long id;
    private String actor;
    private String action;
    private String resourceType;
    private String resourceId;
    private String correlationId;
    private Long durationMs;
    private String result;
    private LocalDateTime createdAt;
    public static AuditRecordResponse from(ControlPlaneAuditRecord controlPlaneAuditRecord){
        return new AuditRecordResponse(controlPlaneAuditRecord.getId(),controlPlaneAuditRecord.getActor(),
                controlPlaneAuditRecord.getAction(),controlPlaneAuditRecord.getResourceType(),controlPlaneAuditRecord.getResourceId(),
                controlPlaneAuditRecord.getCorrelationId(),controlPlaneAuditRecord.getDurationMs(),controlPlaneAuditRecord.getResult(),
                controlPlaneAuditRecord.getCreatedAt());
    }
}
