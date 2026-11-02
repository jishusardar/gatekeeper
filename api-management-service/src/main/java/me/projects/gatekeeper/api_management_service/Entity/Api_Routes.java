package me.projects.gatekeeper.api_management_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "api_routes")
@NoArgsConstructor
public class Api_Routes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id")
    private Organization organization;
    private String name;
    @Column(name = "path_Pattern")
    private String Path_Pattern;
    @Column(name = "target_base_url")
    private String Target_Base_Url;
    @Column(name = "target_path_prefix")
    private String Target_Path_Prefix;
    @Column(name = "request_per_minute")
    private Integer Request_Per_Minutes;
    @Column(name = "response_timeout_ms")
    private Integer Response_Timeout_Ms;
    @Column(name = "idempotency_enabled")
    private boolean Idempotency_Enabled;
    @Column(name = "active")
    private boolean Active;
    @Column(name = "configuration_version")
    private Long Configuration_Version;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "route_id")
    private String routeId;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "routes_allowed_methods",
            joinColumns = @JoinColumn(name = "route_pk"))
    @Column(name = "http_method")
    private Set<String> allowedMethods= new HashSet<>();

    @OneToMany(mappedBy = "apiRoutes",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<RoutesHeaderRules> headerRules=new ArrayList<RoutesHeaderRules>();
}
