CREATE MATERIALIZED VIEW operating_health_facilities_count AS
SELECT
    1 AS id,
    'Pie Chart' AS chart_type,
    COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS operating_count,
    COUNT(f.id) AS total_count,
    now() as date_created
FROM
    facilities f;
