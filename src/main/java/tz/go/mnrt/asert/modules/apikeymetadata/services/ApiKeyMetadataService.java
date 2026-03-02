package tz.go.mnrt.asert.modules.apikeymetadata.services;

import java.util.List;

import tz.go.mnrt.asert.modules.apikeymetadata.entity.ApiKeyMetadata;

public interface ApiKeyMetadataService {

    List<String> getAllowedIps(ApiKeyMetadata metadata) throws Exception;

    void setAllowedIps(ApiKeyMetadata metadata, List<String> allowedIps) throws Exception;

    void incrementUsageCount(ApiKeyMetadata metadata);

    boolean isIpAllowed(ApiKeyMetadata metadata, String ipAddress) throws Exception;

    boolean isWithinRateLimit(ApiKeyMetadata metadata);

    void resetUsageCount(ApiKeyMetadata metadata);
}
