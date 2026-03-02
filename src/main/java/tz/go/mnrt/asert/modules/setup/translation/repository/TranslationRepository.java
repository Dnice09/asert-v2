package tz.go.mnrt.asert.modules.setup.translation.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.translation.entity.Translation;

public interface TranslationRepository extends BaseRepository<Translation, Long> {
  Optional<Translation> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  Optional<Translation> findById(Long id);
}
