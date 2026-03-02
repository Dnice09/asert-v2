alter table assessor_documents
    drop column if exists type;

CREATE TABLE assessor_certifications
(
    id          BIGSERIAL PRIMARY KEY,
    assessor_id BIGINT REFERENCES assessors (id) ON DELETE CASCADE,
    title       VARCHAR(255) not null,
    certificate VARCHAR(255) not null
);

ALTER TABLE assessor_documents
    ADD COLUMN document_type_id BIGINT REFERENCES document_types (id) ON DELETE SET NULL;



