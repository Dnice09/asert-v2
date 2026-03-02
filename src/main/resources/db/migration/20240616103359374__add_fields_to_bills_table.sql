-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE bills
ADD COLUMN financial_year_id INTEGER NOT NULL,
ADD COLUMN admin_hierarchy_id INTEGER NOT NULL,
ADD COLUMN control_number VARCHAR(255),
ADD COLUMN control_number_error VARCHAR(255),
ADD COLUMN facility_id INTEGER NOT NULL;

ALTER TABLE bills
ADD CONSTRAINT fk_financial_year
FOREIGN KEY (financial_year_id) REFERENCES financial_years(id);

ALTER TABLE bills
ADD CONSTRAINT fk_facility
FOREIGN KEY (facility_id) REFERENCES facilities(id);


ALTER TABLE bills
ADD CONSTRAINT fk_admin_hierarchy
FOREIGN KEY (admin_hierarchy_id) REFERENCES admin_hierarchies(id);
