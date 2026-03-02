package tz.go.mnrt.asert.modules.menugroup.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;

public interface MenuGroupRepository extends BaseRepository<MenuGroup, Long> {

  Optional<MenuGroup> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  @Query(
      "select new tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto(g.id, g.name, g.icon,"
          + " g.state, g.sortOrder, g.translationLabel) from MenuGroup g where g.id in :ids order"
          + " by g.sortOrder")
  List<MenuGroupDto> byIds(@Param("ids") List<Long> ids);
}
