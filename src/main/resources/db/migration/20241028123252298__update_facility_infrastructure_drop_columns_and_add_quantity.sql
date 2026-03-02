-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE facility_infrastructures
    ADD COLUMN quantity float null DEFAULT 0.0;

ALTER TABLE facility_infrastructures
    DROP COLUMN label_number;

ALTER TABLE facility_infrastructures
    DROP COLUMN number_of_storeys;

ALTER TABLE facility_infrastructures
    DROP COLUMN construction_start_date;

ALTER TABLE facility_infrastructures
    DROP COLUMN construction_end_date;

ALTER TABLE facility_infrastructures
    DROP COLUMN purchase_date;

ALTER TABLE facility_infrastructures
    DROP COLUMN infrastructure_condition;

ALTER TABLE facility_infrastructures
    DROP COLUMN is_functional;

ALTER TABLE facility_infrastructures
    DROP COLUMN date_of_last_maintenance;

ALTER TABLE facility_infrastructures
    DROP COLUMN supplier_contractor;

ALTER TABLE facility_infrastructures
    DROP COLUMN liability_warrant_end_date;

ALTER TABLE facility_infrastructures
    DROP COLUMN serial_no;

ALTER TABLE facility_infrastructures
    DROP COLUMN is_asset_movable;
