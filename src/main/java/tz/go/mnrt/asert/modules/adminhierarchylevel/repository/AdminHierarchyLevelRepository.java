package tz.go.mnrt.asert.modules.adminhierarchylevel.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

public interface AdminHierarchyLevelRepository extends BaseRepository<AdminHierarchyLevel, Long> {

  Optional<AdminHierarchyLevel> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  List<AdminHierarchyLevel> findAllByOrderByPositionAsc();

    boolean existsByCodeIgnoreCase(String code);

    boolean existsByPosition(int position);

    Optional<AdminHierarchyLevel> findByCode(String code);
}
