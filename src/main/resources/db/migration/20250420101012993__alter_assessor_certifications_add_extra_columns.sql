ALTER TABLE assessor_certifications
    ADD COLUMN if not exists issuer VARCHAR(255) NULL;

ALTER TABLE assessor_certifications
    ADD COLUMN if not exists issue_date DATE NULL;

ALTER TABLE assessor_certifications
    ADD COLUMN if not exists expiry_date DATE;

ALTER TABLE assessor_certifications
    ADD COLUMN if not exists description TEXT;
