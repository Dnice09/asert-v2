package tz.go.mnrt.asert.modules.setup.attachmenttype.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.attachmenttype.entity.AttachmentType;

public interface AttachmentTypeRepository
    extends BaseRepository<AttachmentType, Long> {

    Optional<AttachmentType> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    Optional<AttachmentType> findByCode(String code);
}
