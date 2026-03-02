-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE assessor_rejection_reasons
(
    id         BIGSERIAL PRIMARY KEY,
    uuid       UUID               NOT NULL UNIQUE,
    code       VARCHAR(50) UNIQUE NOT NULL,
    reason     TEXT               NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    is_deleted BOOLEAN DEFAULT FALSE
);





