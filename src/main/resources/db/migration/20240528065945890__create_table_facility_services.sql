CREATE TABLE facility_services
(
    facility_id BIGINT NOT NULL,
    service_id      BIGINT NOT NULL,
    CONSTRAINT pk_facility_services PRIMARY KEY (facility_id, service_id)
);
