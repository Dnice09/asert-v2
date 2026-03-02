package tz.go.mnrt.asert.modules.user.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.entity.User;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findUserByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"adminHierarchy"}, type = EntityGraph.EntityGraphType.FETCH)
    Page<User> findAll(Specification<User> specification, Pageable page);

    @EntityGraph(attributePaths = {"roles", "adminHierarchy"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findById(Long id);

    @Query("Select u from User u "
        + "left join fetch u.roles r "
        + "left join  fetch r.authorities "
        + "left join fetch u.adminHierarchy a"
        + " left join fetch a.adminHierarchyLevel "
        + " where u.email=:email")
    Optional<User> findUserByEmailWithAuthorities(@Param("email") String email);

    @Query("select new tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto(u.id, u.uuid, u.email,"
        + " u.firstName, u.lastName, u.adminHierarchyId) from User u where u.email=:email and"
        + " u.isActive = true")
    Optional<LoggedInUserDto> findLoggedIn(@Param("email") String email);

    @Query("select new tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto(u.id, u.uuid, u.email,"
        + " u.firstName, u.lastName, u.adminHierarchyId) from User u where u.id=:id and"
        + " u.isActive = true")
    Optional<LoggedInUserDto> findLoggedIn(@Param("id") Long id);

    @EntityGraph(attributePaths = {"roles"}, type = EntityGraph.EntityGraphType.FETCH)
    Optional<User> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Modifying
    @Query("update User u set u.password=:newPassword , u.passwordChanged= true where u.uuid=:uuid")
    int changePassword(@Param("uuid") UUID uuid, @Param("newPassword") String newPassword);

    @Modifying
    @Query("update User u set u.password=:defaultPassword, u.passwordChanged= false where u.uuid=:uuid")
    int resetPassword(@Param("uuid") UUID uuid, @Param("defaultPassword") String defaultPassword);

    @Modifying
    @Query("update User u set u.adminHierarchyId=:adminHierarchyId where u.id=:id")
    int updateUserAdminHierarchy(@Param("id") Long id, @Param("adminHierarchyId") Long adminHierarchyId);

    Optional<User> findByEmail(String email);
}
