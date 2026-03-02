-- Enable UUID generation if not already done
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

INSERT INTO document_types (name, created_at, updated_at, is_deleted, uuid)
VALUES ('National ID', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Passport', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('CV', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Degree Certificate', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Diploma Certificate', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Professional Certification', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Police Clearance Certificate', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Code of Conduct Signed Form', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Conflict of Interest Declaration', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Recommendation Letter', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Employment Verification Letter', NOW(), NOW(), FALSE, gen_random_uuid()),
       ('Assessor Badge / Accreditation Card', NOW(), NOW(), FALSE, gen_random_uuid());
