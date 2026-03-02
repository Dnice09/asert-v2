package tz.go.mnrt.asert.modules.adminhierarchy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyMinDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyTreeDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyTreeDtoImpl;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.adminhierarchylevel.repository.AdminHierarchyLevelRepository;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.validation.ValidationException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminHierarchyServiceImpl extends SimpleSearchService<AdminHierarchy>
        implements AdminHierarchyService {

    private final AdminHierarchyRepository adminHierarchyRepository;
    private final AdminHierarchyLevelRepository levelRepository;
    private final EntityManager em;
    private final UserService userService;

    @Override
    public AdminHierarchyDto save(AdminHierarchyDto dto) {
        AdminHierarchy adminHierarchy = new AdminHierarchy();
        if (dto.getUuid() != null) {
            adminHierarchy = adminHierarchyRepository
                    .findByUuid(dto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException("Admin hierarchy with id {" + dto.getUuid() + "}"));
        }
        BeanUtils.copyProperties(dto, adminHierarchy, "uuid");
        adminHierarchy.setAdminHierarchyLevel(
                em.getReference(AdminHierarchyLevel.class, dto.getAdminHierarchyLevelId()));
        if (dto.getParentId() != null) {
            adminHierarchy.setParent(em.getReference(AdminHierarchy.class, dto.getParentId()));
        }

        // get admin hierarchy level
        AdminHierarchyLevel level = levelRepository
                .findById(dto.getAdminHierarchyLevelId())
                .orElseThrow(() -> new ValidationException(
                        "Admin hierarchy level with id {" + dto.getAdminHierarchyLevelId() + "} not found"));

        if (!"001".equals(level.getCode())) {
            String code = generateCodeForNewHierarchy(adminHierarchy);
            adminHierarchy.setCode(code);
            // String path = generatePathForHierarchy(adminHierarchy.getId());
            // adminHierarchy.setPath(path);
        }

        adminHierarchy = adminHierarchyRepository.save(adminHierarchy);

        dto.setId(adminHierarchy.getId());
        /* updateView(); */
        return dto;
    }

    @Override
    public Page<AdminHierarchyDto> findAll(Pageable page, Map<String, String> search) {
        return adminHierarchyRepository
                .findAll(createSpecification(AdminHierarchy.class, search), page)
                .map(AdminHierarchyDto::new);
    }

    @Override
    public AdminHierarchyDto findByUuid(UUID uuid) {
        return adminHierarchyRepository
                .findByUuid(uuid)
                .map(AdminHierarchyDto::new)
                .orElseThrow(() -> new ValidationException("Admin hierarchy with id {" + uuid + "}"));
    }

    @Override
    public AdminHierarchyTreeDto userTree() {
        LoggedInUserDto user = userService.loggedIn().orElse(null);
        Long userAdminHierarchyId = user != null ? user.getAdminHierarchyId() : null;
        return userAdminHierarchyId != null
                ? adminHierarchyRepository.userTree(userAdminHierarchyId)
                : adminHierarchyRepository.userTree();
    }

    @Override
    public List<AdminHierarchyDto> findByParent(Long id) {
        return adminHierarchyRepository.findByParent(id);
    }

    @Override
    public List<AdminHierarchyDto> getChildrenByUuid(UUID uuid) {
        // First find the admin hierarchy by UUID
        AdminHierarchy parent = adminHierarchyRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Admin hierarchy with UUID {" + uuid + "} not found"));
        
        // Then find children by parent ID and ensure no duplicates based on ID
        List<AdminHierarchyDto> children = adminHierarchyRepository.findByParent(parent.getId());
        
        // Remove any potential duplicates based on hierarchy ID
        return children.stream()
                .collect(Collectors.toMap(
                    AdminHierarchyDto::getId,
                    child -> child,
                    (existing, replacement) -> existing // Keep first occurrence in case of duplicates
                ))
                .values()
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    public void delete(UUID uuid) {
        adminHierarchyRepository.deleteByUuid(uuid);
        /* updateView(); */
    }

    @Override
    public List<AdminHierarchy> getByLevelPosition(Integer position) {
        return adminHierarchyRepository.findAdminHierarchiesByAdminHierarchyLevel_Position(position);
    }

    @Async
    @Transactional
    public void updateView() {
        List<AdminHierarchyLevel> adminLevels = levelRepository.findAllByOrderByPositionAsc();
        int totalLevels = adminLevels.size();

        StringBuilder query = new StringBuilder(
                "CREATE MATERIALIZED VIEW vw_admin_hierarchies AS "
                        + "select a.id, "
                        + "a.code, "
                        + "l.position as position, "
                        + "a.name, l.name as level,");

        for (AdminHierarchyLevel yLevel : adminLevels) {
            StringBuilder _case = new StringBuilder("case ");
            StringBuilder _caseName = new StringBuilder("case ");
            int p = 0;

            for (AdminHierarchyLevel xLevel : adminLevels) {
                StringBuilder _then = new StringBuilder(" then ");
                StringBuilder _thenName = new StringBuilder(" then ");

                if (xLevel.getPosition() <= yLevel.getPosition()) {
                    if (yLevel.getPosition() == totalLevels && xLevel.getPosition() == totalLevels) {
                        _then.append("a.id");
                        _thenName.append("a.name");
                    } else {
                        _then.append(" null");
                        _thenName.append(" null");
                    }
                } else {
                    _then.append("p").append(totalLevels - p).append(".id");
                    _thenName.append("p").append(totalLevels - p).append(".name");
                    p++;
                }
                _case.append(" when l.position = ").append(xLevel.getPosition()).append(_then);
                _caseName.append(" when l.position = ").append(xLevel.getPosition()).append(_thenName);
            }
            _case.append(" end as ").append(yLevel.getName()).append("_id");
            _caseName.append(" end as ").append(yLevel.getName()).append("_name");
            if (yLevel.getPosition() < totalLevels) {
                _case.append(",");
                _caseName.append(",");
            } else {
                _case.append(",");
            }
            query.append(_case);
            query.append(_caseName);
        }
        query.append(
                " from admin_hierarchies a "
                        + "join admin_hierarchy_levels as l on l.id = a.admin_hierarchy_level_id");

        query.append(" order by l.position, a.name  ");

        Query qDrop = em.createNativeQuery(" DROP MATERIALIZED VIEW  IF EXISTS  vw_admin_hierarchies");
        qDrop.executeUpdate();

        Query q = em.createNativeQuery(query.toString());
        q.executeUpdate();
    }

    @Override
    public AdminHierarchyDto findById(Long id) {
        return adminHierarchyRepository
                .findById(id)
                .map(AdminHierarchyDto::new)
                .orElseThrow(() -> new ValidationException("Admin hierarchy with id {" + id + "}"));
    }

    @Override
    public Optional<AdminHierarchy> findByCode(String code) {
        return adminHierarchyRepository.findByCode(code);
    }

    @Override
    public Page<AdminHierarchyDto> findAllDistrict(Pageable page, Map<String, String> search) {
        return adminHierarchyRepository
                .findAllDistrict(
                        createSpecification(AdminHierarchy.class, search), page)
                .map(AdminHierarchyDto::new);
    }

    @Override
    public boolean exists(String code) {
        return adminHierarchyRepository.existsByCode(code);
    }

    @Override
    public List<AdminHierarchyMinDto> findAllByPosition(Integer position) {
        return adminHierarchyRepository.findAllByPosition(position);
    }

    @Override
    public AdminHierarchyDto generateCode(UUID uuid) {
        Optional<AdminHierarchy> row = adminHierarchyRepository.findByUuid(uuid);

        if (row.isEmpty()) {
            throw new ValidationException("Admin hierarchy with uuid {" + uuid + "} not found");
        }

        AdminHierarchy adminHierarchy = row.get();

        try {
            String path = adminHierarchyRepository.findPathById(adminHierarchy.getId());
            log.info("Path: {}", path);

            adminHierarchy.setPath(path);

            int count = adminHierarchyRepository.findByParent(adminHierarchy.getParentId()).size();

            log.info("Count: {}", count);

            String code = String.format("0%s", count);

            adminHierarchy.setCode(code);

            AdminHierarchyDto dto = new AdminHierarchyDto();

            BeanUtils.copyProperties(adminHierarchyRepository.save(adminHierarchy), dto);
            return dto;
        } catch (Exception e) {
            log.error("Error executing query: ", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public String generatePathForHierarchy(Long hierarchyId) {
        String sql = "WITH RECURSIVE parents AS ( " +
                "    SELECT id, parent_id, CAST(id AS TEXT) AS path " +
                "    FROM admin_hierarchies " +
                "    WHERE id = ?1 " +
                "    UNION ALL " +
                "    SELECT ah.id, ah.parent_id, (CAST(ah.id AS TEXT) || '->' || p.path) AS path " +
                "    FROM admin_hierarchies ah " +
                "    JOIN parents p ON ah.id = p.parent_id " +
                ") " +
                "SELECT path " +
                "FROM parents " +
                "WHERE parent_id IS NULL";

        return (String) em.createNativeQuery(sql)
                .setParameter(1, hierarchyId)
                .getSingleResult();
    }

    private String generateCodeForNewHierarchy(AdminHierarchy adminHierarchy) {
        if (adminHierarchy.getParent() == null) {
            throw new IllegalArgumentException("New admin hierarchy must have a parent to generate code.");
        }

        Long parentId = adminHierarchy.getParent().getId();

        Optional<String> maxSiblingCode = adminHierarchyRepository.findMaxCodeByParentId(parentId);

        int nextCode = maxSiblingCode
                .map(code -> Integer.parseInt(code) + 1)
                .orElse(1);

        return String.format("%02d", nextCode);
    }

    @Override
    public List<AdminHierarchy> buildAncestralPath(AdminHierarchy hierarchy) {
        List<AdminHierarchy> path = new ArrayList<>();
        AdminHierarchy current = hierarchy;
        while (current != null) {
            path.add(0, current);
            current = current.getParent();
        }
        return path;
    }

    @Override
    public AdminHierarchyTreeDto buildSubtreeWithHighlightedPath(AdminHierarchy root, List<AdminHierarchy> path) {
        AdminHierarchyTreeDtoImpl tree = new AdminHierarchyTreeDtoImpl(root);
        tree.setHighlighted(path.stream().anyMatch(h -> h.getId().equals(root.getId())));

        Set<AdminHierarchyTreeDto.ChildrenDto> children = root.getChildren() != null
                ? root.getChildren().stream()
                        .map(child -> {
                            AdminHierarchyTreeDtoImpl subtree = (AdminHierarchyTreeDtoImpl) buildSubtreeWithHighlightedPath(
                                    child, path);
                            AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childDto = new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(
                                    child);
                            childDto.setHighlighted(subtree.isHighlighted());
                            childDto.setChildren(subtree.getChildren());
                            return childDto;
                        })
                        .collect(Collectors.toSet())
                : Set.of();
        tree.setChildren(children);

        return tree;
    }

    @Override
    public boolean isNodeInTree(AdminHierarchyTreeDto tree, Long hierarchyId) {
        if (tree.getId().equals(hierarchyId)) {
            return true;
        }
        if (tree.getChildren() != null) {
            for (AdminHierarchyTreeDto.ChildrenDto child : tree.getChildren()) {
                if (isNodeInTree(child, hierarchyId)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isNodeInTree(AdminHierarchyTreeDto.ChildrenDto child, Long hierarchyId) {
        if (child.getId().equals(hierarchyId)) {
            return true;
        }
        if (child.getChildren() != null) {
            for (AdminHierarchyTreeDto.ChildrenDto grandChild : child.getChildren()) {
                if (isNodeInTree(grandChild, hierarchyId)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public AdminHierarchyTreeDto expandPathInTree(AdminHierarchyTreeDto tree, List<AdminHierarchy> path) {
        AdminHierarchyTreeDtoImpl result = new AdminHierarchyTreeDtoImpl(new AdminHierarchy());
        result.setId(tree.getId());
        result.setUuid(tree.getUuid());
        result.setName(tree.getName());
        result.setCode(tree.getCode());
        result.setLevelId(tree.getLevelId());
        result.setLevelCode(tree.getLevelCode());
        result.setLevelName(tree.getLevelName());
        result.setLevelPosition(tree.getLevelPosition());
        result.setHighlighted(
                tree instanceof AdminHierarchyTreeDtoImpl && ((AdminHierarchyTreeDtoImpl) tree).isHighlighted());
        result.setExpanded(path.stream().anyMatch(h -> h.getId().equals(tree.getId())));

        Set<AdminHierarchyTreeDto.ChildrenDto> children = tree.getChildren() != null
                ? tree.getChildren().stream()
                        .map(child -> {
                            AdminHierarchyTreeDto.ChildrenDto expandedChild = expandPathInTree(child, path);
                            AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childDto = new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(
                                    new AdminHierarchy());
                            childDto.setId(child.getId());
                            childDto.setUuid(child.getUuid());
                            childDto.setName(child.getName());
                            childDto.setCode(child.getCode());
                            childDto.setLevelId(child.getLevelId());
                            childDto.setLevelCode(child.getLevelCode());
                            childDto.setLevelName(child.getLevelName());
                            childDto.setLevelPosition(child.getLevelPosition());
                            childDto.setHighlighted(
                                    expandedChild instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl &&
                                            ((AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) expandedChild)
                                                    .isHighlighted());
                            childDto.setExpanded(
                                    expandedChild instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl &&
                                            ((AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) expandedChild).isExpanded());
                            childDto.setChildren(expandedChild.getChildren());
                            return childDto;
                        })
                        .collect(Collectors.toSet())
                : Set.of();
        result.setChildren(children);

        return result;
    }

    private AdminHierarchyTreeDto.ChildrenDto expandPathInTree(AdminHierarchyTreeDto.ChildrenDto child,
            List<AdminHierarchy> path) {
        AdminHierarchyTreeDtoImpl.ChildrenDtoImpl result = new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(
                new AdminHierarchy());
        result.setId(child.getId());
        result.setUuid(child.getUuid());
        result.setName(child.getName());
        result.setCode(child.getCode());
        result.setLevelId(child.getLevelId());
        result.setLevelCode(child.getLevelCode());
        result.setLevelName(child.getLevelName());
        result.setLevelPosition(child.getLevelPosition());
        result.setHighlighted(
                child instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl &&
                        ((AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) child).isHighlighted());
        result.setExpanded(path.stream().anyMatch(h -> h.getId().equals(child.getId())));

        Set<AdminHierarchyTreeDto.ChildrenDto> children = child.getChildren() != null
                ? child.getChildren().stream()
                        .map(grandChild -> expandPathInTree(grandChild, path))
                        .collect(Collectors.toSet())
                : Set.of();
        result.setChildren(children);

        return result;
    }

    @Override
    public CustomApiResponse searchTree(String searchTerm, Long hierarchyId, boolean withTrashed) {
        // Step 1: Validate input - either search term or hierarchyId must be provided
        if ((searchTerm == null || searchTerm.isEmpty()) && hierarchyId == null) {
            return CustomApiResponse.badRequest("Either a search term or hierarchyId is required", null);
        }

        // Step 2: Handle search by hierarchyId
        if (hierarchyId != null) {
            // Find the hierarchy by ID
            Optional<AdminHierarchy> hierarchyOpt = withTrashed
                    ? adminHierarchyRepository.findByIdWithRelationsAndDeleted(hierarchyId, true)
                    : adminHierarchyRepository.findByIdWithRelations(hierarchyId);

            if (hierarchyOpt.isEmpty()) {
                return CustomApiResponse.notFound("Hierarchy with ID " + hierarchyId + " not found");
            }

            AdminHierarchy hierarchy = hierarchyOpt.get();

            // Build minimal tree structure from root to target
            Set<Long> foundHierarchyIds = Set.of(hierarchy.getId());
            Long primaryFocusedId = hierarchy.getId(); // For single hierarchy search, this is the focused one
            AdminHierarchyTreeDto minimalTree = buildMinimalTreeToTarget(hierarchy, withTrashed, foundHierarchyIds, primaryFocusedId);
            return CustomApiResponse.ok("Hierarchy tree retrieved successfully", List.of(minimalTree));
        }

        // Step 3: Handle search by search term
        if (searchTerm == null || searchTerm.isEmpty()) {
            return CustomApiResponse.badRequest("Search term is required when hierarchyId is not provided", null);
        }

        // Find all hierarchies matching the search term
        List<AdminHierarchy> matchingHierarchies = withTrashed
                ? adminHierarchyRepository.findByNameContainingAndDeleted(searchTerm, true)
                : adminHierarchyRepository.findByNameContaining(searchTerm);

        if (matchingHierarchies.isEmpty()) {
            return CustomApiResponse.ok("No matching hierarchies found", new ArrayList<>());
        }

        // Step 4: Build minimal trees for each matching hierarchy
        Map<Long, AdminHierarchyTreeDto> rootTrees = new HashMap<>();
        Set<Long> foundHierarchyIds = matchingHierarchies.stream()
                .map(AdminHierarchy::getId)
                .collect(Collectors.toSet());

        // Use the first matching hierarchy as the primary focused result
        Long primaryFocusedId = matchingHierarchies.isEmpty() ? null : matchingHierarchies.get(0).getId();

        for (AdminHierarchy hierarchy : matchingHierarchies) {
            // Build the ancestral path for this hierarchy
            List<AdminHierarchy> path = buildAncestralPath(hierarchy);
            
            if (path.isEmpty()) {
                continue;
            }

            // Get the root of this path
            AdminHierarchy root = path.get(0);
            Long rootId = root.getId();

            // If we haven't processed this root yet, create minimal tree
            if (!rootTrees.containsKey(rootId)) {
                rootTrees.put(rootId, buildMinimalTreeToTarget(hierarchy, withTrashed, foundHierarchyIds, primaryFocusedId));
            } else {
                // Merge this path into existing root tree
                rootTrees.put(rootId, mergePathIntoTree(rootTrees.get(rootId), path, foundHierarchyIds, primaryFocusedId));
            }
        }

        return CustomApiResponse.ok("Hierarchy search results retrieved successfully", 
                new ArrayList<>(rootTrees.values()));
    }

    /**
     * Builds a minimal tree structure from root to target entity with immediate siblings for context
     */
    private AdminHierarchyTreeDto buildMinimalTreeToTarget(AdminHierarchy target, boolean withTrashed, Set<Long> foundHierarchyIds, Long primaryFocusedId) {
        // Build the ancestral path
        List<AdminHierarchy> path = buildAncestralPath(target);
        
        if (path.isEmpty()) {
            return null;
        }

        // Start building from root
        AdminHierarchy root = path.get(0);
        return buildMinimalTreeRecursive(root, path, 0, withTrashed, foundHierarchyIds, primaryFocusedId);
    }

    /**
     * Recursively builds minimal tree showing only the path to target with immediate siblings
     */
    private AdminHierarchyTreeDto buildMinimalTreeRecursive(AdminHierarchy current, 
            List<AdminHierarchy> targetPath, int currentDepth, boolean withTrashed, Set<Long> foundHierarchyIds, Long primaryFocusedId) {
        
        AdminHierarchyTreeDtoImpl tree = new AdminHierarchyTreeDtoImpl(current);
        
        // Mark as highlighted if this node is in the target path OR is a found result
        boolean isInPath = targetPath.stream().anyMatch(h -> h.getId().equals(current.getId()));
        boolean isFoundResult = foundHierarchyIds.contains(current.getId());
        boolean isPrimaryFocus = primaryFocusedId != null && current.getId().equals(primaryFocusedId);
        
        tree.setHighlighted(isInPath || isFoundResult);
        tree.setExpanded(isInPath);
        tree.setFocused(isPrimaryFocus);

        Set<AdminHierarchyTreeDto.ChildrenDto> children = new HashSet<>();

        // If this is the last node in path, include all immediate children
        if (currentDepth == targetPath.size() - 1) {
            // This is the target node, show all its children
            if (current.getChildren() != null) {
                children = current.getChildren().stream()
                        .map(child -> {
                            AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childDto = 
                                    new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(child);
                            boolean childIsFoundResult = foundHierarchyIds.contains(child.getId());
                            boolean childIsPrimaryFocus = primaryFocusedId != null && child.getId().equals(primaryFocusedId);
                            childDto.setHighlighted(childIsFoundResult);
                            childDto.setExpanded(false);
                            childDto.setFocused(childIsPrimaryFocus);
                            childDto.setChildren(new HashSet<>()); // Use mutable HashSet
                            return childDto;
                        })
                        .collect(Collectors.toSet());
            }
        } else if (currentDepth < targetPath.size() - 1) {
            // We're still on the path to target - include next node in path and its siblings
            AdminHierarchy nextInPath = targetPath.get(currentDepth + 1);
            
            if (current.getChildren() != null) {
                for (AdminHierarchy child : current.getChildren()) {
                    AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childDto = 
                            new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(child);
                    
                    boolean childIsFoundResult = foundHierarchyIds.contains(child.getId());
                    boolean childIsPrimaryFocus = primaryFocusedId != null && child.getId().equals(primaryFocusedId);
                    
                    if (child.getId().equals(nextInPath.getId())) {
                        // This child is on the path - recurse and expand it
                        AdminHierarchyTreeDto subtree = buildMinimalTreeRecursive(child, targetPath, currentDepth + 1, withTrashed, foundHierarchyIds, primaryFocusedId);
                        childDto.setHighlighted(true);
                        childDto.setExpanded(true);
                        childDto.setFocused(childIsPrimaryFocus);
                        childDto.setChildren(subtree.getChildren());
                    } else {
                        // This child is a sibling - show it but don't expand
                        childDto.setHighlighted(childIsFoundResult);
                        childDto.setExpanded(false);
                        childDto.setFocused(childIsPrimaryFocus);
                        childDto.setChildren(new HashSet<>()); // Use mutable HashSet
                    }
                    children.add(childDto);
                }
            }
        }

        tree.setChildren(children);
        return tree;
    }

    /**
     * Merges a new path into an existing tree structure
     */
    private AdminHierarchyTreeDto mergePathIntoTree(AdminHierarchyTreeDto existingTree, List<AdminHierarchy> newPath, Set<Long> foundHierarchyIds, Long primaryFocusedId) {
        if (newPath.isEmpty() || !existingTree.getId().equals(newPath.get(0).getId())) {
            return existingTree;
        }

        // Clone the existing tree
        AdminHierarchyTreeDtoImpl mergedTree = new AdminHierarchyTreeDtoImpl(new AdminHierarchy());
        copyTreeProperties(existingTree, mergedTree);

        // Merge the path into the tree
        mergePathRecursive(mergedTree, newPath, 0, foundHierarchyIds, primaryFocusedId);

        return mergedTree;
    }

    /**
     * Recursively merges a path into tree structure
     */
    private void mergePathRecursive(AdminHierarchyTreeDtoImpl tree, List<AdminHierarchy> path, int currentDepth, Set<Long> foundHierarchyIds, Long primaryFocusedId) {
        if (currentDepth >= path.size()) {
            return;
        }

        AdminHierarchy currentPathNode = path.get(currentDepth);
        
        // If this is the current node in path, mark as highlighted and expanded
        if (tree.getId().equals(currentPathNode.getId())) {
            boolean isFoundResult = foundHierarchyIds.contains(tree.getId());
            boolean isPrimaryFocus = primaryFocusedId != null && tree.getId().equals(primaryFocusedId);
            tree.setHighlighted(true || isFoundResult);
            tree.setExpanded(true);
            tree.setFocused(isPrimaryFocus);
            
            if (currentDepth < path.size() - 1) {
                // Continue merging with children
                AdminHierarchy nextPathNode = path.get(currentDepth + 1);
                
                // Find or create child for next path node
                Set<AdminHierarchyTreeDto.ChildrenDto> children = tree.getChildren();
                if (children == null) {
                    children = new HashSet<>();
                    tree.setChildren(children);
                }

                boolean foundChild = false;
                for (AdminHierarchyTreeDto.ChildrenDto child : children) {
                    if (child.getId().equals(nextPathNode.getId())) {
                        // Recurse into existing child
                        if (child instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) {
                            mergePathRecursiveChild((AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) child, path, currentDepth + 1, foundHierarchyIds, primaryFocusedId);
                        }
                        foundChild = true;
                        break;
                    }
                }

                if (!foundChild) {
                    // Create new child for path
                    AdminHierarchyTreeDtoImpl.ChildrenDtoImpl newChild = 
                            new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(nextPathNode);
                    boolean childIsFoundResult = foundHierarchyIds.contains(nextPathNode.getId());
                    boolean childIsPrimaryFocus = primaryFocusedId != null && nextPathNode.getId().equals(primaryFocusedId);
                    newChild.setHighlighted(true || childIsFoundResult);
                    newChild.setExpanded(currentDepth + 1 < path.size() - 1);
                    newChild.setFocused(childIsPrimaryFocus);
                    newChild.setChildren(new HashSet<>()); // Use mutable HashSet
                    children.add(newChild);
                    
                    // Continue merging
                    mergePathRecursiveChild(newChild, path, currentDepth + 1, foundHierarchyIds, primaryFocusedId);
                }
            }
        }
    }

    /**
     * Recursively merges a path into child tree structure
     */
    private void mergePathRecursiveChild(AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childTree, List<AdminHierarchy> path, int currentDepth, Set<Long> foundHierarchyIds, Long primaryFocusedId) {
        if (currentDepth >= path.size()) {
            return;
        }

        AdminHierarchy currentPathNode = path.get(currentDepth);
        
        // If this is the current node in path, mark as highlighted and expanded
        if (childTree.getId().equals(currentPathNode.getId())) {
            boolean isFoundResult = foundHierarchyIds.contains(childTree.getId());
            boolean isPrimaryFocus = primaryFocusedId != null && childTree.getId().equals(primaryFocusedId);
            childTree.setHighlighted(true || isFoundResult);
            childTree.setExpanded(true);
            childTree.setFocused(isPrimaryFocus);
            
            if (currentDepth < path.size() - 1) {
                // Continue merging with children
                AdminHierarchy nextPathNode = path.get(currentDepth + 1);
                
                // Find or create child for next path node
                Set<AdminHierarchyTreeDto.ChildrenDto> children = childTree.getChildren();
                if (children == null) {
                    children = new HashSet<>();
                    childTree.setChildren(children);
                }

                boolean foundChild = false;
                for (AdminHierarchyTreeDto.ChildrenDto child : children) {
                    if (child.getId().equals(nextPathNode.getId())) {
                        // Recurse into existing child
                        if (child instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) {
                            mergePathRecursiveChild((AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) child, path, currentDepth + 1, foundHierarchyIds, primaryFocusedId);
                        }
                        foundChild = true;
                        break;
                    }
                }

                if (!foundChild) {
                    // Create new child for path
                    AdminHierarchyTreeDtoImpl.ChildrenDtoImpl newChild = 
                            new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(nextPathNode);
                    boolean childIsFoundResult = foundHierarchyIds.contains(nextPathNode.getId());
                    boolean childIsPrimaryFocus = primaryFocusedId != null && nextPathNode.getId().equals(primaryFocusedId);
                    newChild.setHighlighted(true || childIsFoundResult);
                    newChild.setExpanded(currentDepth + 1 < path.size() - 1);
                    newChild.setFocused(childIsPrimaryFocus);
                    newChild.setChildren(new HashSet<>()); // Use mutable HashSet
                    children.add(newChild);
                    
                    // Continue merging
                    mergePathRecursiveChild(newChild, path, currentDepth + 1, foundHierarchyIds, primaryFocusedId);
                }
            }
        }
    }

    /**
     * Copies properties from source tree to target tree
     */
    private void copyTreeProperties(AdminHierarchyTreeDto source, AdminHierarchyTreeDtoImpl target) {
        target.setId(source.getId());
        target.setUuid(source.getUuid());
        target.setName(source.getName());
        target.setCode(source.getCode());
        target.setLevelId(source.getLevelId());
        target.setLevelCode(source.getLevelCode());
        target.setLevelName(source.getLevelName());
        target.setLevelPosition(source.getLevelPosition());
        
        if (source instanceof AdminHierarchyTreeDtoImpl) {
            AdminHierarchyTreeDtoImpl sourceImpl = (AdminHierarchyTreeDtoImpl) source;
            target.setHighlighted(sourceImpl.isHighlighted());
            target.setExpanded(sourceImpl.isExpanded());
            target.setFocused(sourceImpl.isFocused());
        }
        
        // Deep copy children
        if (source.getChildren() != null) {
            Set<AdminHierarchyTreeDto.ChildrenDto> copiedChildren = source.getChildren().stream()
                    .map(child -> {
                        AdminHierarchyTreeDtoImpl.ChildrenDtoImpl copiedChild = 
                                new AdminHierarchyTreeDtoImpl.ChildrenDtoImpl(new AdminHierarchy());
                        copiedChild.setId(child.getId());
                        copiedChild.setUuid(child.getUuid());
                        copiedChild.setName(child.getName());
                        copiedChild.setCode(child.getCode());
                        copiedChild.setLevelId(child.getLevelId());
                        copiedChild.setLevelCode(child.getLevelCode());
                        copiedChild.setLevelName(child.getLevelName());
                        copiedChild.setLevelPosition(child.getLevelPosition());
                        
                        if (child instanceof AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) {
                            AdminHierarchyTreeDtoImpl.ChildrenDtoImpl childImpl = 
                                    (AdminHierarchyTreeDtoImpl.ChildrenDtoImpl) child;
                            copiedChild.setHighlighted(childImpl.isHighlighted());
                            copiedChild.setExpanded(childImpl.isExpanded());
                            copiedChild.setFocused(childImpl.isFocused());
                        }
                        
                        // Use mutable HashSet for children
                        copiedChild.setChildren(child.getChildren() != null ? new HashSet<>(child.getChildren()) : new HashSet<>());
                        return copiedChild;
                    })
                    .collect(Collectors.toSet());
            target.setChildren(copiedChildren);
        }
    }
}
