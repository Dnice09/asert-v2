DROP MATERIALIZED VIEW IF EXISTS yearly_upgraded_facilities_count;

CREATE MATERIALIZED VIEW yearly_upgraded_facilities_count AS
SELECT
    1 AS id,
    'Infographic' AS chart_type,
    COUNT(*) AS total_count,
    now() as date_created
FROM
    facilities f
    INNER JOIN applications a ON f.id = a.facility_id
    INNER JOIN application_types ty ON a.application_type_id = ty.id
WHERE
    a.created_at >= DATE_TRUNC('year', CURRENT_DATE)
    AND a.application_status = 'Upgrading';
