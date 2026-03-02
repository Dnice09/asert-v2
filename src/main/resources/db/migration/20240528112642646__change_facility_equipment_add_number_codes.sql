-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
alter table facility_equipments add if not exists equipment_number varchar(255) null;
alter table facility_equipments add if not exists non_equipment_number varchar(255) null;
alter table facility_equipments add if not exists under_maintenance_equipment_number varchar(255) null;
