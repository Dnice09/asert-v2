ALTER TABLE assessors
    ADD COLUMN rejection_reason_id BIGINT REFERENCES assessor_rejection_reasons(id) ON DELETE SET NULL;
