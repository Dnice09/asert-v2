ALTER TABLE facility_levels
    ADD COLUMN level_rank INTEGER null;

ALTER TABLE facility_levels
    ADD COLUMN level_group_id BIGINT null;

ALTER TABLE facility_levels
    ADD COLUMN price NUMERIC(32,2) null;

ALTER TABLE facility_levels
    ADD CONSTRAINT FK_facility_levels_ON_level_group FOREIGN KEY (level_group_id) REFERENCES level_groups (id);

