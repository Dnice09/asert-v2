ALTER TABLE education_courses ADD COLUMN if not exists created_by varchar(255) NULL;
ALTER TABLE education_courses ADD COLUMN if not exists updated_by varchar(255) NULL;
