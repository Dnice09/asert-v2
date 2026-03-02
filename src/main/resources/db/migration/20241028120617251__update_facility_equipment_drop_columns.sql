-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE facility_equipments
    DROP COLUMN if exists bed_capacity;
ALTER TABLE facility_equipments
    DROP COLUMN if exists non_equipment_number;
ALTER TABLE facility_equipments
    DROP COLUMN if exists equipment_number;
ALTER TABLE facility_equipments
    DROP COLUMN if exists under_maintenance_equipment_number;
ALTER TABLE facility_equipments
    DROP COLUMN if exists is_active;
ALTER TABLE facility_equipments
    ADD COLUMN quantity float null DEFAULT 0.0

