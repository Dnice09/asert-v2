-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE hotel_assessors (
    hotel_id BIGINT NOT NULL,
    assessor_id BIGINT NOT NULL,

    -- Set the primary key as the combination of both IDs
    PRIMARY KEY (hotel_id, assessor_id),

    -- Create foreign key constraints
    CONSTRAINT fk_hotel_assessors_hotel
        FOREIGN KEY (hotel_id)
        REFERENCES hotels (id)
        ON DELETE CASCADE,

    CONSTRAINT fk_hotel_assessors_assessor
        FOREIGN KEY (assessor_id)
        REFERENCES assessors (id)
        ON DELETE CASCADE
);

-- Add an index for better query performance
CREATE INDEX idx_hotel_assessors_assessor_id ON hotel_assessors (assessor_id);

-- Add a comment to the table
COMMENT ON TABLE hotel_assessors IS 'Join table for the many-to-many relationship between hotels and assessors';
