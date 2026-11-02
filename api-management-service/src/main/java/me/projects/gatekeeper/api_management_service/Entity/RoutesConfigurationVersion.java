package me.projects.gatekeeper.api_management_service.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "routes_configuration_versions")
@Getter
@Setter
@NoArgsConstructor
public class RoutesConfigurationVersion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id",nullable = false)
    private Organization organization;
    @Column(name = "route_id")
    private String routeId;
    @Column(name = "version_number")
    private Long VersionNumber;
    @Column(name = "change_type")
    private String ChangeType;
    @Column(name = "configuration_snapshot",columnDefinition = "LONGTEXT")
    private String ConfigurationSnapshot;
    private LocalDateTime created_at;
}
