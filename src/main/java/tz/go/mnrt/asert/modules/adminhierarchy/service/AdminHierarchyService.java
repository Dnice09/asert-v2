package tz.go.mnrt.asert.modules.adminhierarchy.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyMinDto;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyTreeDto;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface AdminHierarchyService {

    AdminHierarchyDto save(AdminHierarchyDto dto);

    Page<AdminHierarchyDto> findAll(Pageable page, Map<String, String> search);

    AdminHierarchyDto findByUuid(UUID id);

    AdminHierarchyDto generateCode(UUID id);

    AdminHierarchyDto findById(Long id);

    AdminHierarchyTreeDto userTree();

    List<AdminHierarchyDto> findByParent(Long id);

    List<AdminHierarchyDto> getChildrenByUuid(UUID uuid);

    void delete(UUID uuid);

    List<AdminHierarchy> getByLevelPosition(Integer position);

    boolean exists(String code);

    Optional<AdminHierarchy> findByCode(String code);

    Page<AdminHierarchyDto> findAllDistrict(Pageable page, Map<String, String> search);

    List<AdminHierarchyMinDto> findAllByPosition(Integer position);

    String generatePathForHierarchy(Long hierarchyId);

    List<AdminHierarchy> buildAncestralPath(AdminHierarchy hierarchy);

    AdminHierarchyTreeDto buildSubtreeWithHighlightedPath(AdminHierarchy root, List<AdminHierarchy> path);

    boolean isNodeInTree(AdminHierarchyTreeDto tree, Long hierarchyId);

    AdminHierarchyTreeDto expandPathInTree(AdminHierarchyTreeDto tree, List<AdminHierarchy> path);

    CustomApiResponse searchTree(String searchTerm, Long hierarchyId, boolean withTrashed);
}
