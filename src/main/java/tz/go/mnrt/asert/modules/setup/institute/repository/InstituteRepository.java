package tz.go.mnrt.asert.modules.setup.institute.repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.institute.entity.Institute;

import java.util.Optional;
import java.util.UUID;

public interface InstituteRepository extends BaseRepository<Institute, Long> {
    Optional<Institute> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<Institute> findByNameIgnoreCase(String name);
}
