package tz.go.mnrt.asert.modules.setup.identificationtype.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.identificationtype.entity.IdentificationType;

public interface IdentificationTypeRepository
    extends BaseRepository<IdentificationType, Long> {

  Optional<IdentificationType> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
