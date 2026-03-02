-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO education_levels (uuid, name, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Certificate', NOW(), NOW()),
    (gen_random_uuid(), 'Diploma', NOW(), NOW()),
    (gen_random_uuid(), 'Advanced Diploma', NOW(), NOW()),
    (gen_random_uuid(), 'Bachelor Degree', NOW(), NOW()),
    (gen_random_uuid(), 'Postgraduate Diploma', NOW(), NOW()),
    (gen_random_uuid(), 'Masters Degree', NOW(), NOW()),
    (gen_random_uuid(), 'Doctorate (PhD)', NOW(), NOW());



