package tz.go.mnrt.asert.modules.adminhierarchy.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.configs.NoAuthorization;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.adminhierarchy.dtos.AdminHierarchyDto;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.adminhierarchy.service.AdminHierarchyService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * REST controller for managing administrative hierarchies and HFR code
 * generation.
 */
@RestController
@RequestMapping(Constant.API_V1 + "/admin-hierarchies")
@RequiredArgsConstructor
@Slf4j
public class AdminHierarchyResource {
    private final AdminHierarchyService adminHierarchyService;

    /**
     * Fetches a paginated and searchable list of admin hierarchies.
     *
     * @param pagination pagination and sorting parameters.
     * @param search     map of search criteria.
     * @return paginated list of admin hierarchies.
     */
    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(
                adminHierarchyService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    /**
     * Creates a new administrative hierarchy.
     *
     * @param dto the AdminHierarchyDto containing hierarchy details.
     * @return the created hierarchy details.
     * @throws ValidationException if the dto contains an ID or UUID.
     */
    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody AdminHierarchyDto dto) {
        if (dto.getId() != null || dto.getUuid() != null) {
            throw new ValidationException("New AdminHierarchy cannot contain id or uuid");
        }
        return CustomApiResponse.created(
                "AdminHierarchy created successfully", adminHierarchyService.save(dto));
    }

    /**
     * Updates an existing administrative hierarchy.
     *
     * @param dto  the AdminHierarchyDto containing updated details.
     * @param uuid the UUID of the hierarchy to update.
     * @return the updated hierarchy details.
     * @throws ValidationException if the UUID in the dto does not match the path
     *                             variable.
     */
    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody AdminHierarchyDto dto, @PathVariable UUID uuid) {
        if (dto.getUuid() == null || !Objects.equals(dto.getUuid(), uuid)) {
            throw new ValidationException(
                    "AdminHierarchy id must be present and equals to path id {" + uuid + "}");
        }
        return CustomApiResponse.ok(
                "AdminHierarchy updated successfully", adminHierarchyService.save(dto));
    }

    /**
     * Fetches an admin hierarchy by its UUID.
     *
     * @param uuid the UUID of the hierarchy.
     * @return the requested admin hierarchy details.
     */
    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(adminHierarchyService.findByUuid(uuid));
    }

    /**
     * Fetches an admin hierarchy by its database ID.
     *
     * @param id the database ID of the hierarchy.
     * @return the requested admin hierarchy details.
     */
    @GetMapping("/{id}/fetch")
    public CustomApiResponse getById(@PathVariable("id") Long id) {
        return CustomApiResponse.ok(adminHierarchyService.findById(id));
    }

    /**
     * Generates a unique code for the given admin hierarchy.
     *
     * @param uuid the UUID of the hierarchy.
     * @return the generated code.
     */
    @GetMapping("/{uuid}/generate-code")
    public CustomApiResponse generateCode(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(adminHierarchyService.generateCode(uuid));
    }

    /**
     * Fetches child hierarchies for a given parent.
     *
     * @param parentId the ID of the parent hierarchy.
     * @return list of child hierarchies.
     */
    @GetMapping("/children/{parentId}")
    public CustomApiResponse getChildren(@PathVariable("parentId") Long parentId) {
        return CustomApiResponse.ok(adminHierarchyService.findByParent(parentId));
    }

    /**
     * Fetches child hierarchies for a given parent hierarchy by UUID.
     *
     * @param uuid the UUID of the parent hierarchy.
     * @return list of child hierarchies.
     */
    @GetMapping("/{uuid}/get-children")
    @NoAuthorization
    public CustomApiResponse getChildrenByUuid(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(adminHierarchyService.getChildrenByUuid(uuid));
    }

    /**
     * Fetches the administrative hierarchy tree accessible to the current user.
     *
     * @return the hierarchy tree structure.
     */
    @GetMapping("/user-tree")
    public CustomApiResponse getUserTree() {
        return CustomApiResponse.ok(adminHierarchyService.userTree());
    }

    /**
     * Deletes an administrative hierarchy by its UUID.
     *
     * @param uuid the UUID of the hierarchy to delete.
     * @return confirmation message.
     */
    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        adminHierarchyService.delete(uuid);
        return CustomApiResponse.ok("AdminHierarchy deleted successfully");
    }

    /**
     * Fetches admin hierarchies based on their level position.
     *
     * @param position the level position to fetch.
     * @return list of hierarchies at the specified level.
     */
    @GetMapping("/level/{position}")
    public CustomApiResponse getByLevel(@PathVariable(value = "position") Integer position) {
        List<AdminHierarchy> adminHierarchyList = adminHierarchyService.getByLevelPosition(position);
        return CustomApiResponse.ok("Success", adminHierarchyList);
    }

    /**
     * Fetches all districts with optional search and pagination.
     *
     * @param pagination pagination and sorting parameters.
     * @param search     map of search criteria.
     * @return paginated list of districts.
     */
    @NoAuthorization
    @GetMapping("/get-districts")
    public CustomApiResponse getDistricts(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(
                adminHierarchyService.findAllDistrict(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @GetMapping("/get-regions")
    public CustomApiResponse getRegions() {
        return CustomApiResponse.ok(adminHierarchyService.findAllByPosition(3));
    }

    @NoAuthorization
    @GetMapping("/get-portal-regions")
    public CustomApiResponse getPortalRegions() {
        return CustomApiResponse.ok(adminHierarchyService.findAllByPosition(3));
    }

    @GetMapping("/get-countries")
    public CustomApiResponse getCountries() {
        return CustomApiResponse.ok(adminHierarchyService.findAllByPosition(1));
    }

    /**
     * Fetches all Shehia for a given parent region.
     *
     * @param parentId the ID of the parent region.
     * @return list of Shehia.
     */
    @NoAuthorization
    @GetMapping("/get-shehia-by-parent")
    public CustomApiResponse getShehiaByParent(@RequestParam("parentId") Long parentId) {
        return CustomApiResponse.ok(adminHierarchyService.findByParent(parentId));
    }

    /**
     * Searches admin hierarchies by name or ID and returns them in a recursive tree
     * structure.
     *
     * @param searchTerm  The search term to match against name.
     * @param hierarchyId The ID of the hierarchy to retrieve.
     * @param withTrashed Whether to include soft-deleted records (default: false).
     * @return A CustomApiResponse containing the tree structure.
     */
    @GetMapping("/search-tree")
    @NoAuthorization
    public CustomApiResponse searchTree(
            @RequestParam(value = "search", required = false) String searchTerm,
            @RequestParam(value = "hierarchyId", required = false) Long hierarchyId,
            @RequestParam(value = "withTrashed", defaultValue = "false") boolean withTrashed) {
        return adminHierarchyService.searchTree(searchTerm, hierarchyId, withTrashed);
    }

    @GetMapping("/get-locations")
    @NoAuthorization
    public CustomApiResponse getLocations(Pageable pagination, @RequestParam() Map<String, String> search) {
        return CustomApiResponse.ok(
                adminHierarchyService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

}
