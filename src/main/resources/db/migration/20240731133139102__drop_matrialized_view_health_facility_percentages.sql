DROP MATERIALIZED VIEW IF EXISTS health_facility_percentages;

CREATE MATERIALIZED VIEW health_facility_percentages AS
    SELECT fl.id,
    fl.name,
    'INFOGRAPHIC' AS chart_type,
    COALESCE(COUNT(f.id), 0) AS facility_count,
    now() as date_created
FROM facility_levels fl
    LEFT JOIN facilities f ON f.facility_level_id = fl.id
GROUP BY fl.id, fl.name;


