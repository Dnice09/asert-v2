-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.


ALTER TABLE pre_registrations
    ADD COLUMN if not exists nearest_facility_distance_km float null DEFAULT 0.0;

ALTER TABLE pre_registrations
    ADD COLUMN if not exists service_area_id bigint NULL;

ALTER TABLE facilities
    DROP COLUMN if exists nearest_facility_distance_km;
