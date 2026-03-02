-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE facilities
    DROP COLUMN IF EXISTS catchment_population;
ALTER TABLE facilities
    DROP COLUMN IF EXISTS catchment_area;
ALTER TABLE facilities
    DROP COLUMN IF EXISTS service_population;


ALTER TABLE facilities
    ADD COLUMN catchment_population INTEGER NULL;

ALTER TABLE facilities
    ADD COLUMN catchment_area INTEGER NULL;

ALTER TABLE facilities
    ADD COLUMN service_population INTEGER NULL;
