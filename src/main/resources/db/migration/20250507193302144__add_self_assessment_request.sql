ALTER TABLE assessor_hotels
    ADD COLUMN IF NOT EXISTS self_assessment_request boolean not null default false;
