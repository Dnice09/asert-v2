DROP MATERIALIZED VIEW IF EXISTS mv_facility_distribution_locations;
DROP MATERIALIZED VIEW IF EXISTS yearly_upgraded_facilities_count;
DROP MATERIALIZED VIEW IF EXISTS monthly_registered_facilities_count;
DROP MATERIALIZED VIEW IF EXISTS operating_health_facilities_count;
DROP MATERIALIZED VIEW IF EXISTS health_facility_percentages;
DROP MATERIALIZED VIEW IF EXISTS ownership_type_percentage;

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
    INNER JOIN admin_hierarchies w ON w.id = s.parent_id
    INNER JOIN admin_hierarchies d ON d.id = w.parent_id
    INNER JOIN admin_hierarchies r ON r.id = d.parent_id;

CREATE MATERIALIZED VIEW mv_number_health_facility_upgrades_this_year AS
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

CREATE MATERIALIZED VIEW mv_number_new_facilities_this_month AS
    SELECT
        1 AS id,
        'Infographic' AS chart_type,
        COUNT(*) AS total_count,
        now() as date_created
    FROM
        facilities
    WHERE
        DATE_TRUNC('month', created_at) = DATE_TRUNC('month', CURRENT_DATE);

CREATE MATERIALIZED VIEW mv_proportion_health_facilities_operating_status AS
    SELECT
        1 AS id,
        'Pie Chart' AS chart_type,
        COUNT(CASE WHEN status = 'ACTIVE' THEN 1 END) AS operating_count,
        COUNT(f.id) AS total_count,
        now() as date_created
    FROM
        facilities f;

CREATE MATERIALIZED VIEW mv_proportion_health_facilities_type AS
    SELECT
        fl.id,
        fl.name,
        'INFOGRAPHIC' AS chart_type,
        COALESCE(COUNT(f.id), 0) AS facility_count,
        now() as date_created
    FROM facility_levels fl
        LEFT JOIN facilities f ON f.facility_level_id = fl.id
    GROUP BY fl.id, fl.name;

CREATE MATERIALIZED VIEW mv_proportion_public_health_facilities_ownership AS
    SELECT
        fw.id,
        fw.name,
        'INFOGRAPHIC' AS chart_type,
        COALESCE(COUNT(f.id), 0) AS facility_count,
        now() as date_created
    FROM facility_ownership_categories fw
        LEFT JOIN facilities f ON f.facility_ownership_category_id = fw.id
    GROUP BY fw.id, fw.name;
