-- Simplify assessor certificates structure
-- Drop the evidence table since we just need direct file upload link
DROP TABLE IF EXISTS assessor_certificate_evidence;

-- Add file_upload_id directly to assessor_certificates table
ALTER TABLE assessor_certificates 
ADD COLUMN file_upload_id BIGINT,
ADD CONSTRAINT fk_assessor_certificates_file_upload 
    FOREIGN KEY (file_upload_id) REFERENCES file_uploads (id);

-- Create index for the new foreign key
CREATE INDEX idx_assessor_certificates_file_upload_id ON assessor_certificates (file_upload_id);