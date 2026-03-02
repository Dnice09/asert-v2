package tz.go.mnrt.asert.modules.setup.documenttype.repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.documenttype.entity.DocumentType;

import java.util.Optional;
import java.util.UUID;

public interface DocumentTypeRepository extends BaseRepository<DocumentType, Long> {
    Optional<DocumentType> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<DocumentType> findByNameIgnoreCase(String name);
}
