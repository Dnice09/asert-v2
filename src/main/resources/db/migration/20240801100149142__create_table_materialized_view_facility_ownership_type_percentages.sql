CREATE MATERIALIZED VIEW ownership_type_percentage AS
    SELECT fw.id,
    fw.name,
    'INFOGRAPHIC' AS chart_type,
    COALESCE(COUNT(f.id), 0) AS facility_count,
    now() as date_created
FROM facility_ownership_categories fw
    LEFT JOIN facilities f ON f.facility_ownership_category_id = fw.id
GROUP BY fw.id, fw.name;
