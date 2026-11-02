package me.projects.gatekeeper.api_management_service.Repository;

import me.projects.gatekeeper.api_management_service.Entity.ControlPlaneAuditRecord;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ControlPlaneAuditRepository extends JpaRepository<ControlPlaneAuditRecord,Long> {
    List<ControlPlaneAuditRecord> findAllByOrganizationIdOrderByCreatedAtDesc(Long organizationId);
}
