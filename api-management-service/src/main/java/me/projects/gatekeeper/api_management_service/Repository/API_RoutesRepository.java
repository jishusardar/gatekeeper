package me.projects.gatekeeper.api_management_service.Repository;

import me.projects.gatekeeper.api_management_service.Entity.Api_Routes;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface API_RoutesRepository extends JpaRepository<Api_Routes,Long> {
    boolean existsByOrganizationIdAndRouteId(Long OrganizationId,String RouteId);

    List<Api_Routes> findAllByOrganizationId(Long organizationId);
    Api_Routes findByIdAndOrganizationId(Long id,Long OrganizationId);
    List<Api_Routes> findAllByActiveTrueAndOrganizationStatus(OrganizationStatus OrganizationStatus);
}
