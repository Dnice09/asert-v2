-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Step 1: Rename the existing table
ALTER TABLE role_states RENAME TO role_states_old;

-- Step 2: Create a new table with only the desired columns
CREATE TABLE role_states
(
    role_id BIGINT NOT NULL,
    state VARCHAR(255) NOT NULL,

    CONSTRAINT pk_role_states_new PRIMARY KEY (role_id, state),
    CONSTRAINT fk_role_states_role_id_new FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- Step 3: Copy the data from the renamed table to the new table
INSERT INTO role_states (role_id, state)
SELECT role_id, state
FROM role_states_old;

-- Step 4: Drop the renamed old table
DROP TABLE role_states_old;

