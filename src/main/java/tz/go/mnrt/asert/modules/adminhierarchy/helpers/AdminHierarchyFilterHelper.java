package tz.go.mnrt.asert.modules.adminhierarchy.helpers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;

import java.util.List;

/**
 * Helper class for adding admin hierarchy filters to JPA Specifications.
 * This class is designed to be reusable across various services that need to
 * filter
 * data based on the admin hierarchy associated with the currently logged-in
 * user.
 */
@Component
@Slf4j
public class AdminHierarchyFilterHelper {

    @Lazy
    private final UserService userService;

    private final AdminHierarchyRepository adminHierarchyRepository;

    /**
     * Constructs a new AdminHierarchyFilterHelper.
     *
     * @param userService              the user service to retrieve the logged-in
     *                                 user details.
     * @param adminHierarchyRepository the repository to retrieve admin hierarchy
     *                                 information.
     */
    public AdminHierarchyFilterHelper(UserService userService, AdminHierarchyRepository adminHierarchyRepository) {
        this.userService = userService;
        this.adminHierarchyRepository = adminHierarchyRepository;
    }

    /**
     * Adds an admin hierarchy filter to the given Specification, using the default
     * field name "adminHierarchyId".
     *
     * @param specs the original Specification to which the filter will be added.
     * @param <T>   the type of the entity.
     * @return a new Specification with the admin hierarchy filter added, or the
     * original
     * Specification if no filter is applied.
     */
    public <T> Specification<T> addAdminHierarchyFilter(Specification<T> specs) {
        return addAdminHierarchyFilter(specs, "adminHierarchyId");
    }

    /**
     * Adds an admin hierarchy filter to the given Specification, using the
     * specified
     * field name for the admin hierarchy ID.
     *
     * @param specs                   the original Specification to which the filter
     *                                will be added.
     * @param adminHierarchyFieldName the name of the field in the entity that
     *                                represents the admin hierarchy ID.
     * @param <T>                     the type of the entity.
     * @return a new Specification with the admin hierarchy filter added, or the
     * original
     * Specification if no filter is applied.
     */
    public <T> Specification<T> addAdminHierarchyFilter(Specification<T> specs, String adminHierarchyFieldName) {
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        if (logged != null && logged.getAdminHierarchyId() != null) {
            List<Long> adminIds = adminHierarchyRepository.getUserAdminAreas(logged.getAdminHierarchyId());
            // log.debug("ADMIN_HIERARCHY_IDs: {}", adminIds);
            return specs.and((root, query, cb) -> root.get(adminHierarchyFieldName).in(adminIds));
        }
        return specs;
    }

    public List<Long> getCurrentUserAdminHierarchyIds() {
        List<Long> adminIds = null;
        LoggedInUserDto logged = userService.loggedIn().orElse(null);
        log.info("current user admin hierarchy id {}", logged.getAdminHierarchyId());
        if (logged != null && logged.getAdminHierarchyId() != null) {
            adminIds = adminHierarchyRepository.getUserAdminAreas(logged.getAdminHierarchyId());
        }
        return adminIds;
    }
}
