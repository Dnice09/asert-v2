alter table assessor_education_history
    drop column institute_id;

alter table assessor_education_history
    drop column course_id;

ALTER TABLE assessor_education_history
    ADD COLUMN institution varchar(255) NULL default 'institution';

ALTER TABLE assessor_education_history
    ADD COLUMN course varchar(255) NULL default 'course';


ALTER TABLE assessor_education_history
    ADD COLUMN education_level_id BIGINT REFERENCES education_levels (id) ON DELETE SET NULL;




