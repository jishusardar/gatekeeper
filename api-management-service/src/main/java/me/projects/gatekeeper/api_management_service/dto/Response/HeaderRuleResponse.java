package me.projects.gatekeeper.api_management_service.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.projects.gatekeeper.api_management_service.Entity.RoutesHeaderRules;

@Getter
@AllArgsConstructor
public class HeaderRuleResponse {
    private Long id;
    private String direction;
    private String action;
    private String headerName;
    private String headerValue;
    public static HeaderRuleResponse from(RoutesHeaderRules routesHeaderRules){
        return new HeaderRuleResponse(routesHeaderRules.getId(),routesHeaderRules.getDirection(),routesHeaderRules.getAction(),routesHeaderRules.getHeader_name(),routesHeaderRules.getHeader_value());
    }
}
