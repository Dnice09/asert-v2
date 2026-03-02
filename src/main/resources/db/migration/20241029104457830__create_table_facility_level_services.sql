CREATE TABLE facility_level_services
(
    facility_level_id BIGINT NOT NULL,
    service_id      BIGINT NOT NULL,
    CONSTRAINT pk_facility_level_services PRIMARY KEY (facility_level_id, service_id)
);
