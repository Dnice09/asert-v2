-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE OR REPLACE FUNCTION uid()
    RETURNS text AS
$$
SELECT substring('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ'
                 FROM (random() * 51)::int + 1 for 1) ||
       array_to_string(ARRAY(SELECT substring('abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789'
                                              FROM (random() * 61)::int + 1 FOR 1)
                             FROM generate_series(1, 10)), '')
$$ LANGUAGE sql;

ALTER TABLE admin_hierarchies
    ADD COLUMN integration_code VARCHAR(255) NOT NULL DEFAULT uid();
