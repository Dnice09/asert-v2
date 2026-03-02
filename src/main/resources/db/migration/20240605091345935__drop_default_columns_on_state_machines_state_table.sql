-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE state_machine_states DROP CONSTRAINT pk_state_machine_states;

ALTER TABLE state_machine_states DROP COLUMN id;
ALTER TABLE state_machine_states DROP COLUMN uuid;
ALTER TABLE state_machine_states DROP COLUMN created_at;
ALTER TABLE state_machine_states DROP COLUMN created_by;

ALTER TABLE state_machine_states DROP COLUMN updated_at;
ALTER TABLE state_machine_states DROP COLUMN updated_by;

ALTER TABLE state_machine_states DROP COLUMN is_deleted;

ALTER TABLE state_machine_states
ADD CONSTRAINT pk_state_machine_states PRIMARY KEY (state_machine_id);
