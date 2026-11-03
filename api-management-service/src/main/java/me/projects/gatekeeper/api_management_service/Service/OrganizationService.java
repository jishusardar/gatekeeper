package me.projects.gatekeeper.api_management_service.Service;

import lombok.RequiredArgsConstructor;
import me.projects.gatekeeper.api_management_service.Entity.ManagementUser;
import me.projects.gatekeeper.api_management_service.Entity.Organization;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;
import me.projects.gatekeeper.api_management_service.Entity.Role;
import me.projects.gatekeeper.api_management_service.Event.GatewayConfigurationEventService;
import me.projects.gatekeeper.api_management_service.Exception.ApiException;
import me.projects.gatekeeper.api_management_service.Repository.ManagementUserRepository;
import me.projects.gatekeeper.api_management_service.Repository.OrganizationRepository;
import me.projects.gatekeeper.api_management_service.Repository.RoleRepository;
import me.projects.gatekeeper.api_management_service.dto.Request.TenantAdminRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.TenantOrganizationRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.OrganizationResponse;
import me.projects.gatekeeper.api_management_service.dto.Request.OrganizationRequest;
import me.projects.gatekeeper.api_management_service.dto.Request.OrganizationStatusRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.TenantAdminResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.handler.AbstractDetectingUrlHandlerMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrganizationService {
    private final OrganizationRepository organizationRepository;
    private final ManagementUserRepository managementUserRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final TenantContext tenantContext;
    private final AuditService auditService;
    private final AbstractDetectingUrlHandlerMapping abstractDetectingUrlHandlerMapping;
    private final GatewayConfigurationEventService configurationEventService;

    @Transactional
    public OrganizationResponse create(OrganizationRequest organizationRequest){
        long startedAt=System.nanoTime();
        checkUnique(organizationRequest.getName(),organizationRequest.getSlug(),null);
        Organization organization=new Organization();
        organization.setName(organizationRequest.getName());
        organization.setSlug(organizationRequest.getSlug());
        organization.setStatus(OrganizationStatus.PENDING);
        organization.setCreated_at(LocalDateTime.now());
        organization.setUpdated_at(LocalDateTime.now());
        organizationRepository.save(organization);
        auditService.record(organization,"CREATE_ORGANIZATION","ORGANIZATION",
                organization.getId().toString(),startedAt,"ORGANIZATION CREATED IN PENDING STATUS");
        return OrganizationResponse.from(organization);
    }
    @Transactional(readOnly = true)
    public List<OrganizationResponse> findAll(){
        List<OrganizationResponse> organizationResponseList=new ArrayList<>();
        for (Organization organization:organizationRepository.findAllByStatusNot(OrganizationStatus.DELETED)){
            organizationResponseList.add(OrganizationResponse.from(organization));
        }
        return organizationResponseList;
    }
    @Transactional(readOnly = true)
    public OrganizationResponse findById(Long id){
        return OrganizationResponse.from(getOrganization(id));
    }
    @Transactional
    public OrganizationResponse updateById(Long id,OrganizationRequest organizationRequest){
        Long startedAt=System.nanoTime();
        Organization organization=getOrganization(id);
        checkUnique(organizationRequest.getName(),organizationRequest.getSlug(),id);
        organization.setName(organizationRequest.getName());
        organization.setSlug(organizationRequest.getSlug());
        organization.setUpdated_at(LocalDateTime.now());
        auditService.record(organization,"UPDATE_ORGANIZATION","ORGANIZATION"
                ,id.toString(),startedAt,"Organization Name and Slug Updated");
        configurationEventService.publish("ORGANIZATION_UPDATED",organization.getId(),
                null,null);
        return OrganizationResponse.from(organization);
    }
    @Transactional
    public OrganizationResponse changeStatus(Long id, OrganizationStatusRequest organizationStatusRequest){
        long startedAt=System.nanoTime();
        Organization organization=getOrganization(id);
        if(organizationStatusRequest.getStatus()==OrganizationStatus.DELETED){
            throw new ApiException(HttpStatus.BAD_GATEWAY,"Use Delete To soft-Delete an Organization");
        }
        organization.setStatus(organizationStatusRequest.getStatus());
        organization.setUpdated_at(LocalDateTime.now());
        auditService.record(organization,"CHANGE_ORGANIZATION_STATUS","ORGANIZATION",id.toString(),
                startedAt,"Status Changed to "+organizationStatusRequest.getStatus());
        configurationEventService.publish("ORGANIZATION_"+organization.getStatus(),organization.getId(),
                null,null);
        return OrganizationResponse.from(organization);
    }
    @Transactional
    public void delete(Long id){
        long startedAt=System.nanoTime();
        Organization organization=getOrganization(id);
        organization.setStatus(OrganizationStatus.DELETED);
        organization.setDeleted_at(LocalDateTime.now());
        organization.setUpdated_at(LocalDateTime.now());
        auditService.record(organization,"DELETE_ORGANIZATION","ORGANIZATION"
                ,id.toString(),startedAt,"Organization Soft-Deleted");
        configurationEventService.publish("ORGANIZATION_DELETED",organization.getId(),
                null,null);
    }
    public void checkUnique(String name,String slug,Long currentId){
        if(organizationRepository.existsByNameIgnoreCase(name)){
            Organization organization= currentId == null ? null :organizationRepository.findById(currentId).orElse(null);
            if(organization==null|| !organization.getName().equalsIgnoreCase(name)){
                throw new ApiException(HttpStatus.CONFLICT,"Organization Name Already Exist");
            }
        }
        if(organizationRepository.existsBySlugIgnoreCase(slug)){
            Organization organization= currentId == null ? null: organizationRepository.findById(currentId).orElse(null);
            if(organization==null|| !organization.getSlug().equalsIgnoreCase(slug)){
                throw new ApiException(HttpStatus.CONFLICT,"Slug name Already Exist");
            }
        }
    }
    public Organization getOrganization(Long id){
       return organizationRepository.findByIdAndStatusNot(id,OrganizationStatus.DELETED)
               .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,"Organization Not found"));
    }
    @Transactional
    public TenantAdminResponse createTenantAdmin(Long org_id, TenantAdminRequest tenantAdminRequest){
        Long startedAt=System.nanoTime();
        Organization organization=getOrganization(org_id);
        if(managementUserRepository.findByOrganizationId(org_id).isPresent()){
            throw new ApiException(HttpStatus.CONFLICT,"This Organization already has a tenat administrator");
        }
        if(managementUserRepository.findByUsername(tenantAdminRequest.getUsername()).isPresent()){
            throw new ApiException(HttpStatus.CONFLICT,"Username Already Exist");
        }
        Role role=roleRepository.findByName("TENANT_ADMIN").orElseThrow(
                ()-> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR,"TENANT_ADMIN is Missing"));
        ManagementUser managementUser=new ManagementUser();
        managementUser.setOrganization(organization);
        managementUser.setRole(role);
        managementUser.setUsername(tenantAdminRequest.getUsername());
        managementUser.setPassword_Hash(passwordEncoder.encode(tenantAdminRequest.getPassword()));
        managementUser.setEnabled(tenantAdminRequest.isEnabled());
        managementUser.setCreated_at(LocalDateTime.now());
        managementUser.setUpdated_at(LocalDateTime.now());
        managementUserRepository.save(managementUser);
        auditService.record(managementUser.getOrganization(),"CREATE_TENAT_ADMIN","MANAGEMENT_USER"
                ,managementUser.getId().toString(),startedAt,"Tenant Administrator Created");
        return TenantAdminResponse.from(managementUser);
    }
    @Transactional(readOnly = true)
    public TenantAdminResponse getTenantAdmin(Long org_id){
        ManagementUser managementUser=getTenantAdminEntity(org_id);
        return TenantAdminResponse.from(managementUser);
    }
    @Transactional
    public TenantAdminResponse UpdateTenantAdmin(Long org_id,TenantAdminRequest tenantAdminRequest){
        Long startedAt=System.nanoTime();
        ManagementUser managementUser=getTenantAdminEntity(org_id);
        if(!managementUser.getUsername().equals(tenantAdminRequest.getUsername()) &&
                managementUserRepository.findByUsername(tenantAdminRequest.getUsername()).isPresent()){
            throw new ApiException(HttpStatus.CONFLICT,"Username Already Exists");
        }
        managementUser.setUsername(tenantAdminRequest.getUsername());
        managementUser.setPassword_Hash(passwordEncoder.encode(tenantAdminRequest.getPassword()));
        managementUser.setEnabled(tenantAdminRequest.isEnabled());
        managementUser.setUpdated_at(LocalDateTime.now());
        auditService.record(managementUser.getOrganization(),"UPDATED_TENAT_ADMIN","MANAGEMENT_USER"
                ,managementUser.getId().toString(),startedAt,"Tenant Administrator Updated");
        return TenantAdminResponse.from(managementUser);
    }
    public ManagementUser getTenantAdminEntity(Long org_id){
        return managementUserRepository.findByOrganizationId(org_id)
                .orElseThrow(()-> new ApiException(HttpStatus.NOT_FOUND,"Tenant Admin Not Found"));
    }
    @Transactional
    public void deleteTenantAdmin(Long id) {
        Long startedAt = System.nanoTime();
        ManagementUser managementUser = getTenantAdminEntity(id);
        managementUserRepository.delete(managementUser);
        auditService.record(managementUser.getOrganization(), "DELETE_TENANT_ADMIN", "MANAGEMENT_USER",
                managementUser.getId().toString(), startedAt, "Tenant Administrator Deleted");
    }
    @Transactional(readOnly = true)
    public OrganizationResponse getOwnOrganization(){
        return OrganizationResponse.from(tenantContext.getOrganization());
    }
    @Transactional
    public OrganizationResponse updateOwnOrganization(TenantOrganizationRequest tenantOrganizationRequest){
        long startedAt=System.nanoTime();
        Organization organization=tenantContext.getOrganization();
        if(!organization.getName().equalsIgnoreCase(tenantOrganizationRequest.getName())
                && organizationRepository.existsByNameIgnoreCase(tenantOrganizationRequest.getName())){
            throw new ApiException(HttpStatus.CONFLICT,"Organization Name Already Exist");
        }
        organization.setName(tenantOrganizationRequest.getName());
        organization.setUpdated_at(LocalDateTime.now());
        auditService.record(organization,"UPDATE_OWN_ORGANIZATION","ORGANIZATION",
                organization.getId().toString(),startedAt,"Tenant updated its Organization Name");
        return OrganizationResponse.from(organization);

    }

}
