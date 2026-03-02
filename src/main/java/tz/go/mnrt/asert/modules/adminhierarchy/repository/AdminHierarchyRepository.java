package tz.go.mnrt.asert.modules.adminhierarchy.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyMinDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyTreeDto;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AdminHierarchyRepository extends BaseRepository<AdminHierarchy, Long> {

    @EntityGraph(attributePaths = { "adminHierarchyLevel", "parent" }, type = EntityGraph.EntityGraphType.FETCH)
    Page<AdminHierarchy> findAll(Specification<AdminHierarchy> specification, Pageable pageable);

    @EntityGraph(attributePaths = { "adminHierarchyLevel", "parent" }, type = EntityGraph.EntityGraphType.FETCH)
    Optional<AdminHierarchy> findByUuid(UUID uuid);

    Optional<AdminHierarchy> findById(Long id);

    void deleteByUuid(UUID uuid);

    Optional<AdminHierarchy> findFirstByOrderByIdAsc();

    @Query("Select new tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyDto(a.id, a.uuid,"
            + " a.name, a.code, l.id, l.code, l.name, l.position) from AdminHierarchy a join"
            + " a.adminHierarchyLevel l where a.parent.id =:parentId")
    List<AdminHierarchyDto> findByParent(@Param("parentId") Long parentId);

    @Query("Select a from AdminHierarchy a left join fetch a.adminHierarchyLevel left join fetch"
            + " a.children c left  join fetch c.adminHierarchyLevel where  a.id"
            + " =:userAdminHierarchyId")
    AdminHierarchyTreeDto userTree(Long userAdminHierarchyId);

    @Query("Select a from AdminHierarchy a left join fetch a.adminHierarchyLevel left join fetch"
            + " a.children c left  join fetch c.adminHierarchyLevel where  a.parent.id is null ")
    AdminHierarchyTreeDto userTree();

    List<AdminHierarchy> findAdminHierarchiesByAdminHierarchyLevel_Position(Integer position);

    @Query(value = "WITH RECURSIVE children AS ( "
            + "SELECT id, parent_id, name "
            + "FROM admin_hierarchies "
            + "WHERE id = :userAdminHierarchyId "
            + "UNION "
            + "SELECT o.id, o.parent_id, o.name "
            + "FROM admin_hierarchies o "
            + "INNER JOIN children c ON c.id = o.parent_id "
            + ") SELECT id FROM children ", nativeQuery = true)
    List<Long> getUserAdminAreas(@Param("userAdminHierarchyId") Long userAdminHierarchyId);

    @Query("Select distinct l.position from AdminHierarchy a join a.adminHierarchyLevel l where"
            + " a.id=:adminHierarchyId")
    Integer findAdminPosition(@Param("adminHierarchyId") Long adminHierarchyId);

    boolean existsByCode(String code);

    Optional<AdminHierarchy> findByCode(String code);

    @Query(value = "SELECT * FROM admin_hierarchies ad "
            + "WHERE ad.admin_hierarchy_level_id = 5", nativeQuery = true)
    Page<AdminHierarchy> findAllDistrict(Specification<AdminHierarchy> specification, Pageable pageable);

    @Query("SELECT new tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyMinDto(h.id, h.uuid, h.name, h.code) FROM AdminHierarchy h where h.adminHierarchyLevel.position = :position")
    List<AdminHierarchyMinDto> findAllByPosition(@Param("position") Integer position);

    @Query(value = "WITH RECURSIVE nodes AS ( " +
            "SELECT aa.id, aa.name, aa.parent_id, 1 AS depth, CAST(aa.id AS TEXT) AS path " +
            "FROM admin_hierarchies AS aa " +
            "WHERE aa.id = ?1 " +
            "UNION ALL " +
            "SELECT location.id, location.name, location.parent_id, p.depth + 1 AS depth, " +
            "(p.path || '->' || CAST(location.id AS TEXT)) " +
            "FROM nodes AS p " +
            "JOIN admin_hierarchies AS location ON location.parent_id = p.id " +
            "WHERE p.depth < 5 " +
            ") " +
            "SELECT path FROM nodes WHERE nodes.id = ?1", nativeQuery = true)
    String findDepthPathById(Long id);

    @Query(value = "WITH RECURSIVE nodes AS ( " +
            "SELECT aa.id, aa.name, aa.parent_id, 1 AS depth, CAST(aa.id AS TEXT) AS path " +
            "FROM admin_hierarchies AS aa " +
            "JOIN admin_hierarchy_levels AS ahl ON aa.admin_hierarchy_level_id = ahl.id " +
            "WHERE aa.id = :id AND ahl.position NOT IN (1, 5) " +
            "UNION ALL " +
            "SELECT location.id, location.name, location.parent_id, p.depth + 1 AS depth, " +
            "(p.path || '->' || CAST(location.id AS TEXT)) " +
            "FROM nodes AS p " +
            "JOIN admin_hierarchies AS location ON location.parent_id = p.id " +
            "JOIN admin_hierarchy_levels AS ahl ON location.admin_hierarchy_level_id = ahl.id " +
            "WHERE p.depth < 5 AND ahl.position NOT IN (1, 5) " +
            ") " +
            "SELECT path FROM nodes WHERE nodes.id = :id", nativeQuery = true)
    String findDepthPathByIdExcludeCountryAndWard(@Param("id") Long id);

    @Query("SELECT code FROM AdminHierarchy WHERE id = :id")
    Optional<String> findCodeById(@Param("id") Long id);

    @Query(value = "WITH RECURSIVE nodes AS ( " +
            "SELECT aa.id, aa.parent_id, CAST(aa.id AS TEXT) AS path " +
            "FROM admin_hierarchies AS aa " +
            "WHERE aa.parent_id IS NULL " +
            "UNION ALL " +
            "SELECT location.id, location.parent_id, (p.path || '->' || CAST(location.id AS TEXT)) " +
            "FROM nodes AS p " +
            "JOIN admin_hierarchies AS location ON location.parent_id = p.id " +
            ") " +
            "SELECT path " +
            "FROM nodes " +
            "WHERE id = ?1", nativeQuery = true)
    String findPathById(Long id);

    @Query(value = "WITH RECURSIVE descendants AS ( " +
            "SELECT ah.id, ah.parent_id, ah.admin_hierarchy_level_id " +
            "FROM admin_hierarchies AS ah " +
            "WHERE ah.parent_id = ?1 " +
            "UNION ALL " +
            "SELECT child.id, child.parent_id, child.admin_hierarchy_level_id " +
            "FROM admin_hierarchies AS child " +
            "JOIN descendants AS parent ON child.parent_id = parent.id " +
            ") " +
            "SELECT d.id FROM descendants d " +
            "JOIN admin_hierarchy_levels l ON d.admin_hierarchy_level_id = l.id " +
            "WHERE l.code = ?2", nativeQuery = true)
    List<Long> findAllChildrenIdsByParentIdAndLevelCode(Long parentId, String levelCode);

    @Query("SELECT MAX(CAST(code AS int)) FROM AdminHierarchy WHERE parent.id = :parentId")
    Optional<String> findMaxCodeByParentId(@Param("parentId") Long parentId);

    @Query(value = "SELECT id, name, uuid " +
            "FROM admin_hierarchies " +
            "WHERE LOWER(name) IN :names " +
            "AND admin_hierarchy_level_id = :levelId", nativeQuery = true)
    List<Object[]> findAdminHierarchiesByNameIgnoreCaseAndLevel(
            @Param("names") List<String> names,
            @Param("levelId") Integer levelId);

    @Query("SELECT a FROM AdminHierarchy a " +
            "JOIN AdminHierarchy p ON a.parentId = p.id " +
            "WHERE LOWER(a.name) = LOWER(:childName) AND LOWER(p.name) = LOWER(:parentName)")
    Optional<AdminHierarchy> findByChildNameAndParentNameIgnoreCase(
            @Param("childName") String childName,
            @Param("parentName") String parentName);

    // Fixed methods for recursive tree fetching
    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE a.id = :id")
    Optional<AdminHierarchy> findByIdWithRelations(@Param("id") Long id);

    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE a.id = :id AND a.isDeleted = :isDeleted")
    Optional<AdminHierarchy> findByIdWithRelationsAndDeleted(@Param("id") Long id,
            @Param("isDeleted") boolean isDeleted);

    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE a.parent IS NULL")
    List<AdminHierarchy> findRootNodes();

    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE a.parent IS NULL AND a.isDeleted = :isDeleted")
    List<AdminHierarchy> findRootNodesWithDeleted(@Param("isDeleted") boolean isDeleted);

    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<AdminHierarchy> findByNameContaining(@Param("searchTerm") String searchTerm);

    @EntityGraph(attributePaths = { "parent", "children",
            "adminHierarchyLevel" }, type = EntityGraph.EntityGraphType.FETCH)
    @Query("SELECT a FROM AdminHierarchy a WHERE LOWER(a.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) AND a.isDeleted = :isDeleted")
    List<AdminHierarchy> findByNameContainingAndDeleted(@Param("searchTerm") String searchTerm,
            @Param("isDeleted") boolean isDeleted);
}
