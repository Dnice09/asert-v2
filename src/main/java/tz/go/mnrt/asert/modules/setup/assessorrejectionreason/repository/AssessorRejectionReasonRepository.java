package tz.go.mnrt.asert.modules.setup.assessorrejectionreason.repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.assessorrejectionreason.entity.AssessorRejectionReason;

import java.util.Optional;
import java.util.UUID;

public interface AssessorRejectionReasonRepository extends BaseRepository<AssessorRejectionReason, Long> {
    Optional<AssessorRejectionReason> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
