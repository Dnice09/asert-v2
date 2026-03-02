package tz.go.mnrt.asert.modules.apikey.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;

public interface ApiKeyRepository
        extends BaseRepository<ApiKey, Long> {

    Optional<ApiKey> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    boolean existsByApiKey(String apiKey);

    Optional<ApiKey> findByCode(String code);

    Optional<ApiKey> findByApiKey(String apiKey);

    List<ApiKey> findAllByIsPublishedFalse();

}
