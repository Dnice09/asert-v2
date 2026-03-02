package tz.go.mnrt.asert.modules.assessment.employmenthistory.repository;

import tz.go.mnrt.asert.modules.assessment.employmenthistory.entity.EmploymentHistory;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmploymentHistoryRepository extends BaseRepository<EmploymentHistory, Long> {
    Optional<EmploymentHistory> findByUuid(UUID uuid);
    void deleteByUuid(UUID uuid);
}
