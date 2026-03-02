package tz.go.mnrt.asert.modules.authority.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.authority.entity.Authority;

public interface AuthorityRepository extends BaseRepository<Authority, Long> {
  @Query(
      "Select a.id from Authority a join  a.roles r join r.users u where u.email=:email and"
          + " a.resource=:resource and a.action=:action")
  Optional<Authority> findFirstByUserAndResourceAndAction(
      @Param("email") String email,
      @Param("resource") String resource,
      @Param("action") String action);

  Authority findByResourceAndAction(String resourceName, String actionName);

  Optional<Authority> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  List<Authority> findDistinctByResourceNotNull();

  List<Authority> findAuthoritiesByResource(String resource);
}
