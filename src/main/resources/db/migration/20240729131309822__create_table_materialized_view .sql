CREATE MATERIALIZED VIEW health_facility_percentages AS
SELECT
    fl.name,
    COUNT(f.id) AS facility_count
FROM
    facilities f
    JOIN facility_levels fl ON f.facility_level_id = fl.id
GROUP BY
    fl.name;
