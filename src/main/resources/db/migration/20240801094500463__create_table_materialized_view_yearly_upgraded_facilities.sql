CREATE MATERIALIZED VIEW yearly_upgraded_facilities_count AS
SELECT
    1 AS id,
    'Infographic' AS chart_type,
    COUNT(*) AS total_count,
    now() as date_created
FROM
    facilities
WHERE
    created_at >= DATE_TRUNC('year', CURRENT_DATE);
