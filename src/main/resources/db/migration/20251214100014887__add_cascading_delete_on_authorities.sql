-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- Drop the existing foreign key constraint that links to menu_items
ALTER TABLE menu_item_authorities
    DROP CONSTRAINT fk_meniteaut_on_menu_item;

-- Add the foreign key constraint back with ON DELETE CASCADE
ALTER TABLE menu_item_authorities
    ADD CONSTRAINT fk_meniteaut_on_menu_item
    FOREIGN KEY (menu_item_id)
    REFERENCES menu_items(id)
    ON DELETE CASCADE;
