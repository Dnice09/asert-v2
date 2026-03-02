package tz.go.mnrt.asert.modules.assessment.reference.repository;

import tz.go.mnrt.asert.modules.assessment.reference.entity.AssessorReference;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessorReferenceRepository extends BaseRepository<AssessorReference, Long> {
    Optional<AssessorReference> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
