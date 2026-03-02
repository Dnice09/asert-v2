-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Fix assessor score column types from NUMERIC to DOUBLE PRECISION
-- Hibernate expects Double Java type to map to PostgreSQL float8 (DOUBLE PRECISION), not NUMERIC

ALTER TABLE assessment_variance_logs
    ALTER COLUMN assessor_1_score TYPE DOUBLE PRECISION,
    ALTER COLUMN assessor_2_score TYPE DOUBLE PRECISION,
    ALTER COLUMN assessor_3_score TYPE DOUBLE PRECISION,
    ALTER COLUMN score_difference TYPE DOUBLE PRECISION;

-- Also fix other numeric columns that use Double in Java
ALTER TABLE hotel_assessment_approvals
    ALTER COLUMN final_total_score TYPE DOUBLE PRECISION,
    ALTER COLUMN final_max_score TYPE DOUBLE PRECISION,
    ALTER COLUMN final_percentage TYPE DOUBLE PRECISION;

COMMENT ON COLUMN assessment_variance_logs.assessor_1_score IS 'Score given by assessor 1 (DOUBLE PRECISION for Hibernate Double mapping)';
COMMENT ON COLUMN assessment_variance_logs.score_difference IS 'Maximum difference between assessor scores (DOUBLE PRECISION for Hibernate Double mapping)';