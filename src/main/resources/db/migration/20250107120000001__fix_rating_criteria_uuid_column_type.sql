-- Migration to fix UUID column type in rating_criteria table
-- This addresses the schema validation error where Hibernate expects native UUID type

-- Convert the uuid column from VARCHAR(255) to native UUID type
ALTER TABLE rating_criteria ALTER COLUMN uuid TYPE UUID USING uuid::UUID;