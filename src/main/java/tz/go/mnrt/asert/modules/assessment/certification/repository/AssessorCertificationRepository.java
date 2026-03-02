package tz.go.mnrt.asert.modules.assessment.certification.repository;

import tz.go.mnrt.asert.modules.assessment.certification.entity.AssessorCertification;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessorCertificationRepository extends BaseRepository<AssessorCertification, Long> {
    Optional<AssessorCertification> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
