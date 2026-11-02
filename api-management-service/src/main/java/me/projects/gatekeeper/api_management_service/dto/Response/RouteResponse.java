package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.projects.gatekeeper.api_management_service.Entity.Api_Routes;
import me.projects.gatekeeper.api_management_service.Entity.RoutesHeaderRules;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private Long organizationId;
    private String routeId;
    private String name;
    private String pathPattern;
    private String targetBaseUrl;
    private String targetPathPrefix;
    private Integer requestPerMinute;
    private Integer responseTimeoutMs;
    private boolean idempotencyEnabled;
    private boolean active;
    private Long configurationVersion;
    private Set<String>allowedMethods;
    private List<HeaderRuleResponse> headerRules;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    public static RouteResponse from(Api_Routes routes){
        List<HeaderRuleResponse> rules=new ArrayList<HeaderRuleResponse>();
        for (RoutesHeaderRules rule: routes.getHeaderRules()){
            rules.add(HeaderRuleResponse.from(rule));
        }
        return new RouteResponse(routes.getId(),routes.getOrganization().getId()
                ,routes.getRouteId(),routes.getName(),routes.getPath_Pattern(),routes.getTarget_Base_Url(),
                routes.getTarget_Path_Prefix(),routes.getRequest_Per_Minutes(),routes.getResponse_Timeout_Ms(),routes.isIdempotency_Enabled(),
                routes.isActive(),routes.getConfiguration_Version(),routes.getAllowedMethods(),rules,routes.getCreatedAt(),routes.getUpdatedAt());
    }
}
