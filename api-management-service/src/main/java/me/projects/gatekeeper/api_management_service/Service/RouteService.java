package me.projects.gatekeeper.api_management_service.Service;

import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Entity.*;
import me.projects.gatekeeper.api_management_service.Exception.ApiException;
import me.projects.gatekeeper.api_management_service.Repository.API_RoutesRepository;
import me.projects.gatekeeper.api_management_service.Repository.ControlPlaneAuditRepository;
import me.projects.gatekeeper.api_management_service.Repository.RoutesConfigurationVersionRepository;
import me.projects.gatekeeper.api_management_service.dto.Request.AuditRecordResponse;
import me.projects.gatekeeper.api_management_service.dto.Request.HeaderRuleRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.RouteRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.RouteStatusRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteResponse;
import me.projects.gatekeeper.api_management_service.dto.Response.RouteVersionResponse;
import org.aspectj.weaver.ast.Or;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RouteService {
    private final API_RoutesRepository apiRoutesRepository;
    private final TenantContext tenantContext;
    private final RoutesConfigurationVersionRepository routesConfigurationVersionRepository;
    private final ObjectMapper objectMapper;
    private final AuditService auditService;
    private final ControlPlaneAuditRepository controlPlaneAuditRepository;
    @Transactional
    public RouteResponse create(RouteRequest routeRequest){
        Long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();
        validate(routeRequest,organization);
        if(apiRoutesRepository.existsByOrganizationIdAndRouteId(organization.getId(),routeRequest.getRouteId())){
            throw new ApiException(HttpStatus.CONFLICT,
                    "Route ID already exists for this Organization");
        }
        Api_Routes apiRoutes=new Api_Routes();
        apiRoutes.setOrganization(organization);
        apiRoutes.setConfiguration_Version(1L);
        apiRoutes.setCreatedAt(LocalDateTime.now());
        apply(apiRoutes,routeRequest);
        apiRoutesRepository.save(apiRoutes);
        saveVersion(apiRoutes,"CREATED");
        auditService.record(organization,"CREATE_ROUTE","API_ROUTES",routeRequest.getRouteId()
                ,startedAt,"Route Configuration version 1 created");
        return RouteResponse.from(apiRoutes);
    }
    @Transactional(readOnly = true)
    public List<RouteResponse> findAllForTenant(){
        Organization organization=tenantContext.getOrganization();
        return toResponses(apiRoutesRepository.findAllByOrganizationId(organization.getId()));
    }
    @Transactional(readOnly = true)
    public RouteResponse findById(Long id){
        Organization organization=tenantContext.getOrganization();
        return RouteResponse.from(getOwnedRoute(id,organization.getId()));
    }
    @Transactional
    public RouteResponse update(Long id,RouteRequest routeRequest){
        Long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();
        Api_Routes routes=getOwnedRoute(id,organization.getId());
        if(!routes.getRouteId().equals(routeRequest.getRouteId())){
            throw new ApiException(HttpStatus.BAD_REQUEST,"RouteId can be Changed");
        }
        validate(routeRequest,organization);
        routes.setConfiguration_Version(routes.getConfiguration_Version()+1);
        apply(routes,routeRequest);
        saveVersion(routes,"UPDATED");
        auditService.record(organization,"UPDATE_ROUTES","API_ROUTES",routes.getRouteId()
                ,startedAt,"Route Configuration Version "+routes.getConfiguration_Version()+" created");
        return RouteResponse.from(routes);
    }
    @Transactional
    public RouteResponse changeStatus(Long id, RouteStatusRequest routeStatusRequest){
        Long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();
        Api_Routes routes=getOwnedRoute(id,organization.getId());
        routes.setActive(routeStatusRequest.getActive());
        routes.setConfiguration_Version(routes.getConfiguration_Version()+1);
        routes.setUpdatedAt(LocalDateTime.now());
        String changeType= routeStatusRequest.getActive() ? "ACTIVATED": "DEACTIVATED";
        String action=routeStatusRequest.getActive()?"ACTIVE_ROUTES":"DEACTIVATE_ROUTES";
        saveVersion(routes,changeType);
        auditService.record(organization,action,"API_ROUTES",routes.getRouteId()
                ,startedAt,"Route Configuration Version "+routes.getConfiguration_Version()+" created");
        return RouteResponse.from(routes);
    }
    @Transactional
    public void delete(Long id){
        Long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();
        Api_Routes routes=getOwnedRoute(id,organization.getId());
        routes.setConfiguration_Version(routes.getConfiguration_Version()+1);
        routes.setUpdatedAt(LocalDateTime.now());
        saveVersion(routes,"DELETED");
        auditService.record(organization,"DELETE_ROUTES","API_ROUTES",
                id.toString(),startedAt,"soft Deleted Route "+id);
        apiRoutesRepository.delete(routes);
    }
    public void validate(RouteRequest routeRequest,Organization organization){
        String tenantPrefix="/"+organization.getSlug()+"/";
        if(!routeRequest.getPathPattern().startsWith(tenantPrefix)){
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "PathPattern must Start With The Tenant prefix"+ tenantPrefix);
        }
        if(!routeRequest.getTargetPathPrefix().startsWith("/")){
            throw new ApiException(HttpStatus.BAD_REQUEST,"targetPathPrefix must start with /");
        }
        try {
            URI uri=URI.create(routeRequest.getTargetBaseUrl());
            if(!"http".equalsIgnoreCase(uri.getScheme())&& !"https".equalsIgnoreCase(uri.getScheme())){
                throw new IllegalArgumentException();
            }
        }
        catch (IllegalArgumentException exception){
            throw new ApiException(HttpStatus.BAD_REQUEST,
                    "targetBaseUrl must be a valid HTTP or HTTPS URL");
        }
        normalizeMethods(routeRequest.getAllowedMethods());

        for (HeaderRuleRequest ruleRequest: routeRequest.getHeaderRules()){
            String direction=ruleRequest.getDirection().toUpperCase(Locale.ENGLISH);
            String action=ruleRequest.getAction().toUpperCase(Locale.ENGLISH);
            if(!"REQUEST".equals(direction)&& !"RESPONSE".equals(direction)){
                throw new ApiException(HttpStatus.BAD_REQUEST,
                        "Header direction must be REQUEST or RESPONSE");
            }
            if(!"ADD".equals(action) && !"REMOVE".equals(action)){
                throw new ApiException(HttpStatus.BAD_REQUEST,"Header actions Must Be Add or Remove");
            }
            if("ADD".equals(action)&& (ruleRequest.getHeaderValue()==null || ruleRequest.getHeaderValue().trim().isEmpty())){
                throw new ApiException(HttpStatus.BAD_REQUEST,"ADD header rules requires headerValue");
            }
        }

    }
    public Set<String> normalizeMethods(Set<String> methods){
        Set<String> normalized=new HashSet<String>();
        for (String method: methods){
            String value=method.toUpperCase(Locale.ENGLISH);
            try {
                HttpMethod.valueOf(value);
            } catch (IllegalArgumentException exception) {
                throw new ApiException(HttpStatus.BAD_REQUEST,"Unsupported Http Method: "+method);
            }
            normalized.add(value);
        }
        return normalized;
    }
    public void apply(Api_Routes routes,RouteRequest routeRequest){
        routes.setRouteId(routeRequest.getRouteId());
        routes.setName(routeRequest.getName());
        routes.setPath_Pattern(routeRequest.getPathPattern());
        routes.setTarget_Base_Url(routeRequest.getTargetBaseUrl());
        routes.setTarget_Path_Prefix(routeRequest.getTargetPathPrefix());
        routes.setRequest_Per_Minutes(routeRequest.getRequestPerMinute());
        routes.setResponse_Timeout_Ms(routeRequest.getResponseTimeoutMs());
        routes.setIdempotency_Enabled(routeRequest.isIdempotencyEnabled());
        routes.setActive(routeRequest.isActive());
        routes.setUpdatedAt(LocalDateTime.now());
        routes.setAllowedMethods(normalizeMethods(routeRequest.getAllowedMethods()));

        routes.getHeaderRules().clear();
        for (HeaderRuleRequest ruleRequest: routeRequest.getHeaderRules()){
            RoutesHeaderRules rules=new RoutesHeaderRules();
            rules.setApiRoutes(routes);
            rules.setDirection(ruleRequest.getDirection());
            rules.setAction(ruleRequest.getAction().toUpperCase(Locale.ENGLISH));
            rules.setHeader_name(ruleRequest.getHeaderName());
            rules.setCreated_at(LocalDateTime.now());
            rules.setUpdated_at(LocalDateTime.now());
            routes.getHeaderRules().add(rules);
        }
    }
    public List<RouteResponse> toResponses(List<Api_Routes> routes){
        List<RouteResponse> routeResponses=new ArrayList<RouteResponse>();
        for (Api_Routes route: routes){
            routeResponses.add(RouteResponse.from(route));
        }
        return routeResponses;
    }
    public Api_Routes getOwnedRoute(Long id,Long organizationId){
        return apiRoutesRepository.findByIdAndOrganizationId(id,organizationId);
    }
    @Transactional(readOnly = true)
    public List<RouteResponse> findAllForGateway(){
        return toResponses(
                apiRoutesRepository.findAllByActiveTrueAndOrganizationStatus
                        (OrganizationStatus.ACTIVE));
    }
    public void saveVersion(Api_Routes routes,String changeType){
        RoutesConfigurationVersion configurationVersion=new RoutesConfigurationVersion();
        configurationVersion.setOrganization(routes.getOrganization());
        configurationVersion.setRouteId(routes.getRouteId());
        configurationVersion.setVersionNumber(routes.getConfiguration_Version());
        configurationVersion.setChangeType(changeType);
        configurationVersion.setConfigurationSnapshot(
                objectMapper.writeValueAsString(RouteResponse.from(routes)));
        configurationVersion.setCreated_at(LocalDateTime.now());
        routesConfigurationVersionRepository.save(configurationVersion);
    }
    public List<RouteVersionResponse> getVersions(String routeId){
        Organization organization=tenantContext.getOrganization();
        List<RouteVersionResponse> versionResponses=new ArrayList<RouteVersionResponse>();
        List<RoutesConfigurationVersion> versions=routesConfigurationVersionRepository
                .findAllByOrganizationIdAndRouteIdOrderByVersionNumberDesc(organization.getId(),routeId);
        for (RoutesConfigurationVersion version:versions){
            versionResponses.add(RouteVersionResponse.from(version));
        }
        return versionResponses;
    }
    public List<AuditRecordResponse> getAllRecords(){
        Organization organization=tenantContext.getOrganization();
        List<AuditRecordResponse> recordResponses=new ArrayList<AuditRecordResponse>();
        List<ControlPlaneAuditRecord> records=controlPlaneAuditRepository.
                findAllByOrganizationIdOrderByCreatedAtDesc(organization.getId());
        for (ControlPlaneAuditRecord record: records){
            recordResponses.add(AuditRecordResponse.from(record));
        }
        return recordResponses;
    }
}
