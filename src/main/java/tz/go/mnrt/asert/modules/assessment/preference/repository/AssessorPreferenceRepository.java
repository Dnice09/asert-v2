package tz.go.mnrt.asert.modules.assessment.preference.repository;

import tz.go.mnrt.asert.modules.assessment.preference.entity.AssessorPreference;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessorPreferenceRepository extends BaseRepository<AssessorPreference, Long> {
    Optional<AssessorPreference> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
