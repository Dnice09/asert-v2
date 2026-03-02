ALTER TABLE facilities ADD COLUMN facility_type_id BIGINT NOT NULL;

ALTER TABLE facilities
    ADD CONSTRAINT FK_facilities_ON_facility_type FOREIGN KEY (facility_type_id) REFERENCES facility_types (id);

ALTER TABLE facilities ADD COLUMN facility_level_id BIGINT NOT NULL;

ALTER TABLE facilities
    ADD CONSTRAINT FK_facilities_ON_facility_level FOREIGN KEY (facility_level_id) REFERENCES facility_levels (id);

ALTER TABLE facilities ADD COLUMN facility_ownership_category_id BIGINT NOT NULL;

ALTER TABLE facilities
    ADD CONSTRAINT FK_facilities_ON_facility_ownership_category FOREIGN KEY (facility_ownership_category_id) REFERENCES facility_ownership_categories (id);

ALTER TABLE facilities ADD COLUMN facility_ownership_authority_id BIGINT NOT NULL;

ALTER TABLE facilities
    ADD CONSTRAINT FK_facilities_ON_facility_ownership_authority FOREIGN KEY (facility_ownership_authority_id) REFERENCES facility_ownership_authorities (id);
