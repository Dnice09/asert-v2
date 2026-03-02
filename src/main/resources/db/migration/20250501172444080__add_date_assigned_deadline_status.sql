ALTER TABLE assessor_hotels
    ADD COLUMN id BIGSERIAL PRIMARY KEY;

ALTER TABLE assessor_hotels
    ADD COLUMN if not exists date_assigned date NULL;

ALTER TABLE assessor_hotels
    ADD COLUMN if not exists deadline date NULL;

ALTER TABLE assessor_hotels
    ADD COLUMN if not exists status VARCHAR(128) NULL;
