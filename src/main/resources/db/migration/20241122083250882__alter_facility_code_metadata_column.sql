-- Change facilitycode metadata column VARCHAR whatever to JSONB
ALTER TABLE facility_codes ALTER COLUMN metadata TYPE JSONB USING metadata::JSONB;
