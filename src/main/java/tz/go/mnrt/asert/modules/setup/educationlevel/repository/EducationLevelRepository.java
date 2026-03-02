package tz.go.mnrt.asert.modules.setup.educationlevel.repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.educationlevel.entity.EducationLevel;

import java.util.Optional;
import java.util.UUID;

public interface EducationLevelRepository extends BaseRepository<EducationLevel, Long> {
    Optional<EducationLevel> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<EducationLevel> findByNameIgnoreCase(String name);
}
