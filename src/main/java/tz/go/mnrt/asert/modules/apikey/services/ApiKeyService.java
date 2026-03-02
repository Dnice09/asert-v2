package tz.go.mnrt.asert.modules.apikey.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyRequestDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyResponseDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyStatusDto;

public interface ApiKeyService {

    ApiKeyRequestDto save(ApiKeyRequestDto apiKeyDto);

    Page<ApiKeyResponseDto> findAll(Pageable page, Map<String, String> search);

    ApiKeyResponseDto findByUuid(UUID uuid);

    ApiKeyResponseDto changeStatus(ApiKeyStatusDto apiKeyApproveDto);

    void delete(UUID uuid);

    String generateUniqueApiKey();
}
