ALTER TABLE assessor_rejection_reasons ADD COLUMN IF NOT EXISTS created_by VARCHAR(255) NULL;
ALTER TABLE assessor_rejection_reasons ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255) NULL;
