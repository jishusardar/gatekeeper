package me.projects.gatekeeper.api_management_service.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Entity.ControlPlaneAuditRecord;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import me.projects.gatekeeper.api_management_service.Repository.ControlPlaneAuditRepository;
import me.projects.gatekeeper.api_management_service.dto.Request.AuditRecordResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuditService {
    private final ControlPlaneAuditRepository controlPlaneAuditRepository;
    public void record(Organization organization,String action,
                        String resourceType,String resourceId,
                        Long startedAt,String details){
        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        String actor=authentication==null? "System": authentication.getName();
        ControlPlaneAuditRecord controlPlaneAuditRecord=new ControlPlaneAuditRecord();
        controlPlaneAuditRecord.setOrganization(organization);
        controlPlaneAuditRecord.setAction(action);
        controlPlaneAuditRecord.setActor(actor);
        controlPlaneAuditRecord.setResourceType(resourceType);
        controlPlaneAuditRecord.setResourceId(resourceId);
        controlPlaneAuditRecord.setDurationMs(System.nanoTime()-startedAt/1000000);
        controlPlaneAuditRecord.setDetails(details);
        controlPlaneAuditRecord.setResult("SUCCESS");
        controlPlaneAuditRecord.setCorrelationId(getCorrelationId());
        controlPlaneAuditRecord.setCreatedAt(LocalDateTime.now());
        controlPlaneAuditRepository.save(controlPlaneAuditRecord);
    }
    public String getCorrelationId(){
        if(!(RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes)){
            return null;
        }
        HttpServletRequest httpServletRequest=((ServletRequestAttributes)
                RequestContextHolder.getRequestAttributes()).getRequest();
        return httpServletRequest.getHeader("X-Correlation-ID");
    }
}
