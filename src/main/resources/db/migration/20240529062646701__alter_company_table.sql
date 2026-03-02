-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
alter table companies add if not exists address varchar(255) NOT NULL;
alter table companies add if not exists website varchar(255) NULL;
alter table companies add if not exists phone varchar(255) NOT NULL;
alter table companies add if not exists email varchar(255) NOT NULL UNIQUE;
