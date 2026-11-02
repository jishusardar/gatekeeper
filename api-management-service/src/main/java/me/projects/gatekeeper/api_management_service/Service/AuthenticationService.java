package me.projects.gatekeeper.api_management_service.Service;

import me.projects.gatekeeper.api_management_service.Entity.ManagementUser;
import me.projects.gatekeeper.api_management_service.Entity.OrganizationStatus;
import me.projects.gatekeeper.api_management_service.Exception.ApiException;
import me.projects.gatekeeper.api_management_service.Repository.ManagementUserRepository;
import me.projects.gatekeeper.api_management_service.dto.Request.LoginRequest;
import me.projects.gatekeeper.api_management_service.dto.Response.LoginResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthenticationService {
    private final ManagementUserRepository managementUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final Long expirationSeconds;

    public AuthenticationService(ManagementUserRepository managementUserRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder,@Value("${security.jwt.expiration-seconds}") Long expirationSeconds) {
        this.managementUserRepository = managementUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.expirationSeconds = expirationSeconds;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest loginRequest){
        ManagementUser managementUser=managementUserRepository.findByUsername(loginRequest.getUsername()).orElseThrow(()-> invalidcredentials());
        if(!managementUser.isEnabled()|| !passwordEncoder.matches(loginRequest.getPassword(),managementUser.getPassword_Hash())){
            throw invalidcredentials();
        }
        if(managementUser.getOrganization()!=null&& managementUser.getOrganization().getStatus()!= OrganizationStatus.ACTIVE){
            throw new ApiException(HttpStatus.FORBIDDEN,"THe Organization Is Not Active");
        }
        Instant issuedAt =Instant.now();
        String role=managementUser.getRole().getName();
        Long organizationId=managementUser.getOrganization()==null? null: managementUser.getOrganization().getId();
        JwtClaimsSet.Builder claims= JwtClaimsSet.builder()
                .issuer("gatekeeper-api-management")
                .issuedAt(issuedAt)
                .expiresAt(issuedAt.plusSeconds(expirationSeconds))
                .subject(managementUser.getUsername())
                .claim("role",role);
        if(organizationId!=null){
            claims.claim("organizationId",organizationId);
        }
        JwsHeader header=JwsHeader.with(MacAlgorithm.HS256).build();
        String token=jwtEncoder.encode(
                JwtEncoderParameters.from(header,claims.build())).getTokenValue();
        return new LoginResponse(token,"Bearer",expirationSeconds,role,organizationId);
    }
    public RuntimeException invalidcredentials(){
        return new ApiException(HttpStatus.UNAUTHORIZED,"Invalid username or password");
    }
}
