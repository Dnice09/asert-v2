DROP MATERIALIZED VIEW IF EXISTS mv_facility_distribution_locations;

CREATE MATERIALIZED VIEW mv_facility_distribution_locations AS
    SELECT
    f.name AS facility_name,
    fl.name AS level_name,
    s.name AS shehia_name,
    d.name AS district_name,
    r.name as region_name,
    r.iso_code AS region_iso_code
FROM facilities f
    LEFT JOIN facility_levels fl ON f.facility_level_id = fl.id
    INNER JOIN admin_hierarchies s ON f.admin_hierarchy_id = s.id
    INNER JOIN admin_hierarchies d ON d.id = s.parent_id
    INNER JOIN admin_hierarchies r ON r.id = d.parent_id;
