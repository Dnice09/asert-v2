package tz.go.mnrt.asert.modules.role.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.role.entity.Role;

public interface RoleRepository extends BaseRepository<Role, Long> {
    Optional<Role> findRoleByName(String name);

    @EntityGraph(attributePaths = { "level" }, type = EntityGraph.EntityGraphType.FETCH)
    Page<Role> findAll(Specification<Role> specification, Pageable pageable);

    @EntityGraph(attributePaths = { "level", "authorities" }, type = EntityGraph.EntityGraphType.FETCH)
    Optional<Role> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    @Query("Select distinct r from Role r join r.level rl where rl.position >=:userPosition")
    Set<Role> findByUserPosition(@Param("userPosition") Integer userPosition);

    @Query("FROM Role r where r.name in (:roles)")
    List<Role> findByNames(@Param("roles") List<String> roles);


  @Query("SELECT r.name AS roleName, s FROM Role r JOIN r.states s")
    List<Object[]> findRoleStates();
}
