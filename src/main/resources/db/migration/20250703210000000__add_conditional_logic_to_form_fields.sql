-- Add conditional_logic column to form_fields table to support conditional field logic
-- This column will store JSON representation of conditional logic rules for form fields

ALTER TABLE form_fields ADD COLUMN conditional_logic TEXT;

-- Add comment to document the purpose of the column
COMMENT ON COLUMN form_fields.conditional_logic IS 'JSON representation of conditional logic rules that control field visibility and behavior based on other field values';