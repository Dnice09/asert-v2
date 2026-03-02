TRUNCATE TABLE license_renewals RESTART IDENTITY;

WITH numbered_facilities AS (SELECT id,
                                    ROW_NUMBER() OVER (ORDER BY id) AS row_num
                             FROM facilities),
     inserted_licenses AS (
         INSERT INTO license_renewals (facility_id, license_number, request_date, date_renewed, expiry_date, amount,
                                       bill_uuid, uuid, is_deleted)
             SELECT id,
                    'PHAB/' || LPAD(row_num::TEXT, 4, '0') || '/2024' AS license_number,
                    '2024-01-01 00:00:00'                             AS request_date,
                    '2024-01-01 00:00:00'                             AS date_renewed,
                    '2025-01-31 23:59:59'                             AS expiry_date,
                    0                                                 AS amount,
                    NULL                                              AS bill_uuid,
                    gen_random_uuid()                                 AS uuid,
                    false                                             as is_deleted
             FROM numbered_facilities
             RETURNING facility_id, license_number, request_date, date_renewed, expiry_date, amount, bill_uuid, uuid,is_deleted)

UPDATE facilities AS f
SET license_number = il.license_number,
    expiry_date    = '2025-01-31 23:59:59'
FROM inserted_licenses AS il
WHERE f.id = il.facility_id;
