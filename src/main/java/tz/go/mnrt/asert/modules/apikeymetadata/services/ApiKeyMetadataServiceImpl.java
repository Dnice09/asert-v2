package tz.go.mnrt.asert.modules.apikeymetadata.services;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import tz.go.mnrt.asert.modules.apikeymetadata.entity.ApiKeyMetadata;
import tz.go.mnrt.asert.modules.apikeymetadata.repository.ApiKeyMetadataRepository;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;

@Service
@RequiredArgsConstructor
public class ApiKeyMetadataServiceImpl extends SimpleSearchService<ApiKeyMetadata> implements ApiKeyMetadataService {

    private final ApiKeyMetadataRepository apiKeyMetadataRepository;

    @Override
    public List<String> getAllowedIps(ApiKeyMetadata metadata) throws Exception {
        return metadata.getAllowedIps();
    }

    @Override
    public void setAllowedIps(ApiKeyMetadata metadata, List<String> allowedIps) throws Exception {
        metadata.setAllowedIps(allowedIps);
    }

    @Override
    public void incrementUsageCount(ApiKeyMetadata metadata) {
        metadata.setUsageCount(metadata.getUsageCount() + 1);
        apiKeyMetadataRepository.save(metadata);
    }

    @Override
    public boolean isIpAllowed(ApiKeyMetadata metadata, String ipAddress) throws Exception {
        List<String> allowedIps = getAllowedIps(metadata);
        return allowedIps.contains(ipAddress);
    }

    @Override
    public boolean isWithinRateLimit(ApiKeyMetadata metadata) {
        LocalDateTime now = LocalDateTime.now();
        // Reset the usage count if the current time is past the reset time
        if (now.isAfter(metadata.getRateLimitReset())) {
            resetUsageCount(metadata);
        }

        return metadata.getUsageCount() < metadata.getRateLimit();
    }

    @Override
    public void resetUsageCount(ApiKeyMetadata metadata) {
        metadata.setUsageCount(0L);
        metadata.setRateLimitReset(LocalDateTime.now().plusHours(1)); // Reset every hour, adjust as needed
        apiKeyMetadataRepository.save(metadata);
    }
}
