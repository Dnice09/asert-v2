ALTER TABLE assessor_certificates
    ALTER COLUMN created_by TYPE VARCHAR(255) USING (created_by::TEXT),
    ALTER COLUMN updated_by TYPE VARCHAR(255) USING (updated_by::TEXT);
