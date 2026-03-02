ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS file_path varchar(255) NULL default 'file.pdf';
ALTER TABLE assessor_documents
    ADD COLUMN IF NOT EXISTS file_type varchar(255) NULL default 'pdf';
