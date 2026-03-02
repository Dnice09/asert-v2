ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS created_at timestamp not null default CURRENT_TIMESTAMP;
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS updated_at timestamp null;
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS is_deleted boolean not null default false;
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS created_by VARCHAR(255) NULL;
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS updated_by VARCHAR(255) NULL;
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS uuid UUID not null default gen_random_uuid();


