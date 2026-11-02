package me.projects.gatekeeper.api_management_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "routes_header_rules")
@NoArgsConstructor
public class RoutesHeaderRules {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_pk",nullable = true)
    private Api_Routes apiRoutes;
    private String direction;
    private String action;
    private String header_name;
    private String header_value;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
