-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- ========================================
-- Migration: Clean up duplicate approved submissions per assessor
-- Purpose: Ensure only ONE submission per (form, hotel, assessor) combination
-- ========================================
-- Background:
-- Previously, assessors could submit multiple assessments for the same hotel and form.
-- When multiple submissions were approved, they ALL affected the hotel's rating calculation (incorrect).
-- This migration ensures only the MOST RECENT submission per assessor is retained.
-- ========================================

-- Step 1: Identify and mark duplicate submissions as deleted
-- Keep only the MOST RECENT submission (by approved_at, then submitted_at, then id)
WITH ranked_submissions AS (
    SELECT
        id,
        uuid,
        form_id,
        hotel_id,
        assessor_id,
        status,
        submitted_at,
        approved_at,
        is_deleted,
        -- Rank submissions: 1 = most recent (keep), 2+ = older (delete)
        ROW_NUMBER() OVER (
            PARTITION BY form_id, hotel_id, assessor_id
            ORDER BY
                -- Prioritize by approved_at if available, otherwise submitted_at
                COALESCE(approved_at, submitted_at) DESC NULLS LAST,
                -- Use ID as final tiebreaker (higher ID = more recent)
                id DESC
        ) as row_num
    FROM form_submissions
    WHERE is_deleted = false
        AND assessor_id IS NOT NULL
        AND hotel_id IS NOT NULL
        AND form_id IS NOT NULL
),
duplicates_to_delete AS (
    -- Select all submissions except the most recent one (row_num = 1)
    SELECT
        id,
        uuid,
        form_id,
        hotel_id,
        assessor_id,
        status,
        row_num
    FROM ranked_submissions
    WHERE row_num > 1  -- Keep row_num = 1 (most recent), mark others as deleted
)
UPDATE form_submissions
SET
    is_deleted = true,
    updated_at = NOW()
FROM duplicates_to_delete
WHERE form_submissions.id = duplicates_to_delete.id;

-- Step 2: Log the cleanup results for audit purposes
DO $$
DECLARE
    deleted_count INTEGER;
    affected_hotels INTEGER;
    affected_assessors INTEGER;
BEGIN
    -- Count total submissions marked as deleted in this migration
    SELECT COUNT(*) INTO deleted_count
    FROM form_submissions
    WHERE is_deleted = true
        AND updated_at >= NOW() - INTERVAL '1 minute';

    -- Count affected hotels
    SELECT COUNT(DISTINCT hotel_id) INTO affected_hotels
    FROM form_submissions
    WHERE is_deleted = true
        AND updated_at >= NOW() - INTERVAL '1 minute';

    -- Count affected assessors
    SELECT COUNT(DISTINCT assessor_id) INTO affected_assessors
    FROM form_submissions
    WHERE is_deleted = true
        AND updated_at >= NOW() - INTERVAL '1 minute';

    RAISE NOTICE '========================================';
    RAISE NOTICE 'Duplicate Submission Cleanup Complete';
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Total duplicate submissions marked as deleted: %', deleted_count;
    RAISE NOTICE 'Number of hotels affected: %', affected_hotels;
    RAISE NOTICE 'Number of assessors affected: %', affected_assessors;
    RAISE NOTICE '========================================';
    RAISE NOTICE 'Note: Only the most recent submission per (form, hotel, assessor) was retained.';
    RAISE NOTICE 'Old submissions are marked as deleted (soft delete) for audit trail.';
END $$;
