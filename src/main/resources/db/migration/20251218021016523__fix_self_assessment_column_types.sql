-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Fix self_assessments column types from NUMERIC to DOUBLE PRECISION
ALTER TABLE self_assessments
    ALTER COLUMN total_score TYPE DOUBLE PRECISION,
    ALTER COLUMN max_possible_score TYPE DOUBLE PRECISION,
    ALTER COLUMN percentage TYPE DOUBLE PRECISION;

-- Fix self_assessment_scores column types from NUMERIC to DOUBLE PRECISION
ALTER TABLE self_assessment_scores
    ALTER COLUMN score TYPE DOUBLE PRECISION,
    ALTER COLUMN max_possible TYPE DOUBLE PRECISION,
    ALTER COLUMN percentage TYPE DOUBLE PRECISION;