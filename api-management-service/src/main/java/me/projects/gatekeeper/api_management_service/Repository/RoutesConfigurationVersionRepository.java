package me.projects.gatekeeper.api_management_service.Repository;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import me.projects.gatekeeper.api_management_service.Entity.RoutesConfigurationVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoutesConfigurationVersionRepository extends JpaRepository<RoutesConfigurationVersion,Long> {
    List<RoutesConfigurationVersion> findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(Long organizationId, String routeId);
}
