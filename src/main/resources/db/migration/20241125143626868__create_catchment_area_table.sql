-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE facility_catchment_areas
(
    facility_id        BIGINT NOT NULL,
    admin_hierarchy_id BIGINT NOT NULL,
    CONSTRAINT pk_facility_catchment_area PRIMARY KEY (facility_id, admin_hierarchy_id),
    CONSTRAINT fk_admin_hierarchy_on_catchment_area FOREIGN KEY (admin_hierarchy_id) REFERENCES admin_hierarchies (id),
    CONSTRAINT fk_facility_on_catchment_area FOREIGN KEY (facility_id) REFERENCES facilities (id)
);
