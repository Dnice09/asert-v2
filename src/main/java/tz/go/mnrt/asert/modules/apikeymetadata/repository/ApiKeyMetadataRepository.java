package tz.go.mnrt.asert.modules.apikeymetadata.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;
import tz.go.mnrt.asert.modules.apikeymetadata.entity.ApiKeyMetadata;

public interface ApiKeyMetadataRepository
        extends BaseRepository<ApiKeyMetadata, Long> {

    Optional<ApiKey> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);
}
