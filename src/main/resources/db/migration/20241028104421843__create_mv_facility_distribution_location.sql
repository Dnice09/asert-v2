-- WHEN COMMITTING OR REVIEWING THIS FILE: Make sure that the timestamp in the file name (that serves as a version) is the latest timestamp, and that no new migration have been added in the meanwhile.
-- Adding migrations out of order may cause this migration to never execute or behave in an unexpected way.
-- Migrations should NOT BE EDITED. Add a new migration to apply changes.
CREATE MATERIALIZED VIEW mv_facility_distribution_locations AS
    SELECT
    f.name AS facility_name,
    fl.name AS level_name,
    s.name AS shehia_name,
    d.name AS district_name,
    r.name as region_name
FROM facilities f
    LEFT JOIN facility_levels fl ON f.facility_level_id = fl.id
    INNER JOIN admin_hierarchies s ON f.admin_hierarchy_id = s.id
    INNER JOIN admin_hierarchies d ON d.id = s.parent_id
    INNER JOIN admin_hierarchies r ON r.id = d.parent_id;
