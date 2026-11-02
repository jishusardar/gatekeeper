CREATE TABLE organization(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150)  NOT NULL ,
    slug VARCHAR(150) NOT NULL ,
    status VARCHAR(150) NOT NULL ,
    created_at DATE NOT NULL ,
    deleted_at DATE NOT NULL,
    CONSTRAINT pk_organization PRIMARY KEY (id),
    CONSTRAINT uq_organization_name UNIQUE (name),
    CONSTRAINT uq_organization_slug UNIQUE (slug)
);
CREATE TABLE roles(
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    CONSTRAINT pk_roles PRIMARY KEY (id)
);
CREATE TABLE management_user(
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT UNIQUE,
    role_id BIGINT NOT NULL,
    username VARCHAR(150) NOT NULL ,
    Password_Hash VARCHAR(150) NOT NULL ,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT pk_management_user PRIMARY KEY (id),
    CONSTRAINT uq_management_user_username UNIQUE (username),
    CONSTRAINT fk_management_user_organization_id FOREIGN KEY (organization_id) REFERENCES organization(id),
    CONSTRAINT fk_management_user_role_id FOREIGN KEY (role_id) REFERENCES roles(id)
);
CREATE TABLE api_routes(
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL,
    name VARCHAR(150) NOT NULL,
    path_Pattern VARCHAR(300) NOT NULL ,
    target_base_url VARCHAR(300) NOT NULL ,
    target_path_prefix VARCHAR(300) NOT NULL ,
    request_per_minute INTEGER NOT NULL ,
    response_timeout_ms INTEGER NOT NULL ,
    idempotency_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    active BOOLEAN NOT NULL DEFAULT FALSE,
    configuration_version BIGINT NOT NULL ,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_api_routes PRIMARY KEY (id),
    CONSTRAINT uk_api_routes_tenant_route UNIQUE (organization_id),
    CONSTRAINT fk_api_routes_organization FOREIGN KEY (organization_id) REFERENCES organization(id)
);
CREATE TABLE routes_allowed_methods(
    route_id BIGINT NOT NULL ,
    http_method VARCHAR(10) NOT NULL ,
    CONSTRAINT pk_routes_allowed_methods PRIMARY KEY (route_id,http_method),
    CONSTRAINT fk_routes_allowed_methods FOREIGN KEY (route_id) REFERENCES api_routes(id) ON DELETE CASCADE
);
CREATE TABLE routes_configuration_versions(
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT NOT NULL ,
    route_id BIGINT NOT NULL ,
    version_number VARCHAR(100) NOT NULL ,
    change_type VARCHAR(30) NOT NULL ,
    configuration_snapshot LONGTEXT NOT NULL ,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_routes_configuration_versions PRIMARY KEY (id),
    CONSTRAINT uk_route_versions_tenant_versions UNIQUE (organization_id,route_id,version_number),
    CONSTRAINT fk_route_versions_organization FOREIGN KEY (organization_id) REFERENCES organization(id)
);
CREATE TABLE routes_header_rules(
    id BIGINT NOT NULL AUTO_INCREMENT,
    route_id BIGINT NOT NULL ,
    direction VARCHAR(20) NOT NULL ,
    action VARCHAR(20) NOT NULL ,
    header_name VARCHAR(200) NOT NULL ,
    header_value VARCHAR(2000),
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_routes_header_rules PRIMARY KEY (id),
    CONSTRAINT fk_routes_header_rules FOREIGN KEY (route_id) REFERENCES api_routes(id)
);
CREATE TABLE control_plane_audit_records(
    id BIGINT NOT NULL AUTO_INCREMENT,
    organization_id BIGINT,
    actor VARCHAR(100) NOT NULL ,
    action VARCHAR(100) NOT NULL ,
    resource_type VARCHAR(100) NOT NULL ,
    resource_id VARCHAR(100) NOT NULL ,
    correlation_id VARCHAR(100),
    duration_ms BIGINT NOT NULL,
    result VARCHAR(20) NOT NULL,
    details VARCHAR(2000),
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_control_plane_audit_records PRIMARY KEY (id),
    CONSTRAINT fk_audit_organization FOREIGN KEY (organization_id) REFERENCES organization(id)
);