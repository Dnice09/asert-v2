CREATE MATERIALIZED VIEW monthly_registered_facilities_count AS
SELECT
    1 AS id,
    'Infographic' AS chart_type,
    COUNT(*) AS total_count,
    now() as date_created
FROM
    facilities
WHERE
    DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE);
