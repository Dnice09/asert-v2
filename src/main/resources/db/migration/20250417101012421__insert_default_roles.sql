-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

-- assessor documents tables - Stores assessors documents info


insert into roles(UUID, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY, NAME, CODE, LEVEL_ID, IS_DELETED, IS_CLIENT)
values (gen_random_uuid(), CURRENT_TIMESTAMP, 'Kachinga', CURRENT_TIMESTAMP, 'Kachinga','ASSESSOR','ASSESSOR',2,false,false);
insert into roles(UUID, CREATED_AT, CREATED_BY, UPDATED_AT, UPDATED_BY, NAME, CODE, LEVEL_ID, IS_DELETED, IS_CLIENT)
values (gen_random_uuid(), CURRENT_TIMESTAMP, 'Kachinga', CURRENT_TIMESTAMP, 'Kachinga','FACILITY_OWNER','FACILITY_OWNER',2,false,false);






