-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Drop the existing foreign key constraint (must be done before adding the new one)
ALTER TABLE menu_items
    DROP CONSTRAINT fk_menu_items_on_menu_group;

-- Add the foreign key constraint back with ON DELETE CASCADE
ALTER TABLE menu_items
    ADD CONSTRAINT fk_menu_items_on_menu_group
    FOREIGN KEY (menu_group_id)
    REFERENCES menu_groups(id)
    ON DELETE CASCADE;
