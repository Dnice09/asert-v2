-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "pgcrypto"; -- For generating UUIDs

-- Create the table
CREATE TABLE facility_codes (
    id SERIAL PRIMARY KEY,
    uuid UUID NOT NULL DEFAULT gen_random_uuid(),
    hfr_code VARCHAR(50) NOT NULL,
    system_code VARCHAR(50) NOT NULL,
    system_name VARCHAR(100) NOT NULL,
    facility_code VARCHAR(100) NOT NULL,
    version INT NOT NULL DEFAULT 1,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    metadata JSONB DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    deactivated_at TIMESTAMP DEFAULT NULL
);

-- Enforce data integrity: Foreign key for hfr_code if linked to a facilities table
ALTER TABLE facility_codes
ADD CONSTRAINT fk_hfr_code FOREIGN KEY (hfr_code)
REFERENCES facilities(hfr_code);

-- Unique index to ensure no duplicate records per HFR code, system, and facility code
CREATE UNIQUE INDEX idx_facility_unique_code
    ON facility_codes (hfr_code, system_code, facility_code);

-- Indexes for efficient queries
CREATE INDEX idx_facility_hfr_code
    ON facility_codes (hfr_code);

CREATE INDEX idx_facility_is_active
    ON facility_codes (is_active);

CREATE INDEX idx_facility_system_code
    ON facility_codes (system_code);

-- Trigger function to auto-update `updated_at` timestamp
CREATE OR REPLACE FUNCTION update_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger to apply the timestamp update logic
CREATE TRIGGER set_updated_at
BEFORE UPDATE ON facility_codes
FOR EACH ROW
EXECUTE FUNCTION update_timestamp();
