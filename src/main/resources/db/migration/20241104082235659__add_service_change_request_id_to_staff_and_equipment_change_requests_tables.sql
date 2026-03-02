-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
--
ALTER TABLE staff_change_requests
    ADD COLUMN service_change_request_id BIGINT;

ALTER TABLE staff_change_requests
    ADD CONSTRAINT fk_staff_change_requests_service_change_request_id_
    FOREIGN KEY (service_change_request_id) REFERENCES service_change_requests(id);


ALTER TABLE equipment_change_requests
    ADD COLUMN service_change_request_id BIGINT;

ALTER TABLE equipment_change_requests 
    ADD CONSTRAINT fk_equipment_change_requests_service_change_request_id_
    FOREIGN KEY (service_change_request_id) REFERENCES service_change_requests(id);
