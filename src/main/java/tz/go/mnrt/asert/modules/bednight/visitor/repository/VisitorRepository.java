package tz.go.mnrt.asert.modules.bednight.visitor.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.bednight.visitor.entity.Visitor;

public interface VisitorRepository
    extends BaseRepository<Visitor, Long> {

  Optional<Visitor> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
