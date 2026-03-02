-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

CREATE TABLE self_assessment_drafts (
    id BIGSERIAL PRIMARY KEY,
    uuid UUID NOT NULL UNIQUE DEFAULT gen_random_uuid(),
    form_id BIGINT NOT NULL REFERENCES forms(id),
    hotel_id BIGINT REFERENCES hotels(id),
    submitted_by VARCHAR(255) NOT NULL,
    current_section_index INTEGER NOT NULL DEFAULT 0,
    form_data TEXT,
    last_saved_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completion_percentage DOUBLE PRECISION DEFAULT 0.0,
    total_sections INTEGER DEFAULT 0,
    is_deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_by VARCHAR(255)
);

-- Create indexes for better performance
CREATE INDEX idx_self_assessment_drafts_form_id ON self_assessment_drafts(form_id);
CREATE INDEX idx_self_assessment_drafts_hotel_id ON self_assessment_drafts(hotel_id);
CREATE INDEX idx_self_assessment_drafts_submitted_by ON self_assessment_drafts(submitted_by);
CREATE INDEX idx_self_assessment_drafts_last_saved_at ON self_assessment_drafts(last_saved_at);
CREATE INDEX idx_self_assessment_drafts_is_deleted ON self_assessment_drafts(is_deleted);

-- Create unique constraint to prevent duplicate drafts for same form/user/hotel combination
CREATE UNIQUE INDEX idx_unique_self_assessment_draft
ON self_assessment_drafts(form_id, hotel_id, submitted_by)
WHERE is_deleted = FALSE;
