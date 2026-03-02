-- Create assessor_certificates table
CREATE TABLE assessor_certificates (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID DEFAULT gen_random_uuid() UNIQUE NOT NULL,
    certificate_name VARCHAR(255) NOT NULL,
    issuing_authority VARCHAR(255) NOT NULL,
    certificate_number VARCHAR(100),
    issue_date DATE,
    expiry_date DATE,
    description TEXT,
    certificate_type VARCHAR(50),
    verification_status VARCHAR(20) DEFAULT 'PENDING',
    verification_notes TEXT,
    is_active BOOLEAN DEFAULT true NOT NULL,
    assessor_id BIGINT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT,
    updated_by BIGINT,
    is_deleted BOOLEAN DEFAULT false,
    CONSTRAINT fk_assessor_certificate_assessor FOREIGN KEY (assessor_id) REFERENCES assessors (id)
);

-- Create indexes
CREATE INDEX idx_assessor_certificates_assessor_id ON assessor_certificates (assessor_id);
CREATE INDEX idx_assessor_certificates_certificate_type ON assessor_certificates (certificate_type);
CREATE INDEX idx_assessor_certificates_verification_status ON assessor_certificates (verification_status);
CREATE INDEX idx_assessor_certificates_is_active ON assessor_certificates (is_active);
CREATE INDEX idx_assessor_certificates_expiry_date ON assessor_certificates (expiry_date);