package tz.go.mnrt.asert.modules.assessment.document.repository;

import tz.go.mnrt.asert.modules.assessment.document.entity.AssessorDocument;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessorDocumentRepository extends BaseRepository<AssessorDocument, Long> {
    Optional<AssessorDocument> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
