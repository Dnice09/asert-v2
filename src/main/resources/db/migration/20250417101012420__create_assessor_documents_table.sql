-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- assessor documents tables - Stores assessors documents info

CREATE TABLE IF NOT EXISTS document_types
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID         NOT NULL UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE,
    created_by VARCHAR(255),
    updated_at TIMESTAMP WITHOUT TIME ZONE,
    updated_by VARCHAR(255),
    is_deleted BOOLEAN,
    name       VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS assessor_documents
(
    id          BIGSERIAL PRIMARY KEY,
    assessor_id INT REFERENCES assessors (id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    file_path   VARCHAR(255) NOT NULL,
    file_type   VARCHAR(20) DEFAULT 'pdf',
    type_id     INT REFERENCES document_types (id) ON DELETE CASCADE,
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    verified BOOLEAN DEFAULT FALSE
);

-- Foreign key indexes (only create if not exists)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_class WHERE relname = 'idx_documents_assessor_id') THEN
        CREATE INDEX idx_documents_assessor_id ON assessor_documents (assessor_id);
    END IF;
END
$$;

-- Add constraint only if it doesn't exist
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'chk_assessor_file_type'
    ) THEN
        ALTER TABLE assessor_documents
            ADD CONSTRAINT chk_assessor_file_type
            CHECK (file_type IN ('pdf', 'image'));
    END IF;
END
$$;
