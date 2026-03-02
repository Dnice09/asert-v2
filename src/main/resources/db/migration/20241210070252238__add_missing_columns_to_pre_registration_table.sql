-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Migration: Add columns to pre_registration table
ALTER TABLE pre_registrations
    ADD COLUMN service_population INTEGER;

ALTER TABLE pre_registrations
    ADD COLUMN catchment_area INTEGER NULL;

ALTER TABLE pre_registrations
    ADD COLUMN catchment_population INTEGER;

ALTER TABLE pre_registrations
    ADD COLUMN ref_hosp_id BIGINT;

ALTER TABLE pre_registrations
    ADD COLUMN ref_hosp_distance_km INTEGER;

ALTER TABLE pre_registrations
    ADD COLUMN ref_hosp_owner VARCHAR;

ALTER TABLE pre_registrations
    ADD COLUMN referral_point_challenges VARCHAR NULL;

ALTER TABLE pre_registrations
    ADD COLUMN is_board_active BOOLEAN;

ALTER TABLE pre_registrations
    ADD COLUMN is_open_weekend BOOLEAN;

ALTER TABLE pre_registrations
    ADD COLUMN is_open_full_day BOOLEAN;

ALTER TABLE pre_registrations
    ADD COLUMN is_open_late_night BOOLEAN;
