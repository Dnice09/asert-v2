-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
ALTER TABLE room_types ADD COLUMN IF NOT EXISTS bed_room_type_id BIGINT DEFAULT NULL;

ALTER TABLE room_types
    ADD CONSTRAINT fk_room_types_bed_room_type_id
        FOREIGN KEY (bed_room_type_id)
            REFERENCES bed_room_types (id);
