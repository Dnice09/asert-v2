-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.

ALTER TABLE bills
    ADD paid_amount NUMERIC(32,2) NOT NULL DEFAULT 0.0;

ALTER TABLE bills
    ADD receipt_number VARCHAR(255);

ALTER TABLE bills
    ADD currency VARCHAR(255);

ALTER TABLE bills
     ADD transaction_datetime TIMESTAMP WITHOUT TIME ZONE;

ALTER TABLE bills
    ADD psp_name VARCHAR(255);

ALTER TABLE bills
    ADD control_acc_no VARCHAR(255);

ALTER TABLE bills
    RENAME COLUMN amount TO billed_amount;

