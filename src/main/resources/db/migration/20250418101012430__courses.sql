-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE education_courses
(
    id                 BIGSERIAL PRIMARY KEY,
    uuid               UUID         NOT NULL UNIQUE,
    name               VARCHAR(255) NOT NULL, -- e.g. "Bachelor of Hospitality Management"
    education_level_id BIGINT       NOT NULL REFERENCES education_levels (id) ON DELETE CASCADE,
    created_at         TIMESTAMP,
    updated_at         TIMESTAMP,
    is_deleted         BOOLEAN DEFAULT FALSE
);

ALTER TABLE assessor_education_history
    ADD COLUMN course_id BIGINT REFERENCES education_courses(id) ON DELETE SET NULL;

ALTER TABLE assessor_education_history DROP COLUMN education_level_id;





