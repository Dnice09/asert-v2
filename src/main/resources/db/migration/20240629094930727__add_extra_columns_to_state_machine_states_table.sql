-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE state_machine_states
ADD COLUMN extended_state_variables JSONB,
ADD COLUMN event VARCHAR(255),
ADD COLUMN context_obj VARCHAR(255),
ALTER COLUMN state TYPE VARCHAR(255);
