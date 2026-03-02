package tz.go.mnrt.asert.modules.apikey.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyStatusDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyPublishDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyRequestDto;
import tz.go.mnrt.asert.modules.apikey.dtos.ApiKeyResponseDto;
import tz.go.mnrt.asert.modules.apikey.entity.ApiKey;
import tz.go.mnrt.asert.modules.apikey.enums.ApiKeyStatus;
import tz.go.mnrt.asert.modules.apikey.repository.ApiKeyRepository;
import tz.go.mnrt.asert.modules.apikeymetadata.entity.ApiKeyMetadata;
import tz.go.mnrt.asert.modules.apikeymetadata.repository.ApiKeyMetadataRepository;
import tz.go.mnrt.asert.modules.apikeymetadata.services.ApiKeyMetadataService;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApiKeyServiceImpl extends SimpleSearchService<ApiKey> implements ApiKeyService {
    private final ApiKeyRepository apiKeyRepository;
    private final ApiKeyMetadataRepository apiKeyMetadataRepository;
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private final ApiKeyMetadataService apiKeyMetadataService;

    @Value("${spring.rabbitmq.queue.apikey-published-queue}")
    private String apikeyPublishedQueue;

    @Override
    public ApiKeyRequestDto save(ApiKeyRequestDto apiKeyRequestDto) {
        ApiKey apiKey = new ApiKey();
        if (apiKeyRequestDto.getUuid() != null) {
            apiKey = apiKeyRepository
                    .findByUuid(apiKeyRequestDto.getUuid())
                    .orElseThrow(
                            () -> new ValidationException(
                                    "ApiKey with uuid {" + apiKeyRequestDto.getUuid() + "} not found"));
        }

        if (!isValidIPAddress(apiKeyRequestDto.getSystemIp())) {
            throw new ValidationException("IP Address Entered is not valid");
        }

        ApiKeyRequestDto _apiKeyRequestDto = sanitizeRequest(apiKeyRequestDto);

        BeanUtils.copyProperties(_apiKeyRequestDto, apiKey, "uuid");

        apiKey.setStatus(ApiKeyStatus.REGISTERED.toString());

        LocalDateTime expiryDate = LocalDateTime.now().plusYears(1);
        apiKey.setExpiryDate(expiryDate);
        apiKey.setIsApproved(false);
        apiKey.setIsRetired(false);
        apiKey.setIsPublished(false);

        String _apiKey = generateUniqueApiKey();
        apiKey.setApiKey(_apiKey);

        assert (apiKey.getUuid() != null);
        apiKey = apiKeyRepository.save(apiKey);

        // _apiKeyRequestDto.setId(apiKey.getId());

        // Publish the new ApiKey to API_KEY_PUBLISHED queue with REGISTERED as header
        // Map<String, Object> messageHeader = Map.of("message_type", "REGISTERED");
        // queuePublisher.publishToQueue(APIKEY_PUBLISHED, apiKey, messageHeader);

        ApiKeyPublishDto apiKeyToPublish = new ApiKeyPublishDto();
        apiKeyToPublish.setSystemName(apiKey.getSystemName());
        apiKeyToPublish.setCode(apiKey.getCode());
        apiKeyToPublish.setApiKey(_apiKey);
        apiKeyToPublish.setSystemIp(apiKey.getSystemIp());
        apiKeyToPublish.setExpiryDate(apiKey.getExpiryDate());

        // Set is_published status to true
        apiKey.setIsPublished(true);
        apiKeyRepository.save(apiKey);

        return apiKeyRequestDto;
    }

    @Override
    public Page<ApiKeyResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated ApiKeys with page {} and search {} ", page, search);
        return apiKeyRepository
                .findAll(createSpecification(ApiKey.class, search), page)
                .map(ApiKeyResponseDto::new);
    }

    @Override
    public ApiKeyResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return apiKeyRepository
                .findByUuid(uuid)
                .map(ApiKeyResponseDto::new)
                .orElseThrow(() -> new ValidationException("ApiKey with uuid {" + uuid + "} not found"));
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting ApiKey with uuid {} ", uuid);
        apiKeyRepository.softDelete(uuid);
    }

    @Override
    public String generateUniqueApiKey() {
        String apiKey;
        do {
            byte[] randomBytes = new byte[32];
            secureRandom.nextBytes(randomBytes);
            apiKey = base64Encoder.encodeToString(randomBytes);
        } while (apiKeyRepository.existsByApiKey(apiKey));
        return apiKey;
    }

    @Override
    public ApiKeyResponseDto changeStatus(ApiKeyStatusDto apiKeyStatusDto) {
        Optional<ApiKey> apiKey = apiKeyRepository.findByUuid(apiKeyStatusDto.getApiKeyUuid());

        if (apiKey.isEmpty()) {
            throw new ValidationException("ApiKey with uuid {" + apiKeyStatusDto.getApiKeyUuid() + "} not found");
        }

        ApiKey api = apiKey.get();
        // Determine the status based on the conditions
        String apiKeyStatus;
        if (apiKeyStatusDto.getIsRetired()) {
            apiKeyStatus = ApiKeyStatus.RETIRED.toString();
        } else if (apiKeyStatusDto.getIsApproved()) {
            apiKeyStatus = ApiKeyStatus.APPROVED.toString();
        } else {
            apiKeyStatus = ApiKeyStatus.DEACTIVATED.toString();
        }
        // Set the determined status
        api.setStatus(apiKeyStatus);
        api.setIsApproved(apiKeyStatusDto.getIsApproved());
        api.setIsRetired(apiKeyStatusDto.getIsRetired());

        // save the changes
        ApiKey createdApiKey = apiKeyRepository.save(api);

        String _apiKey = api.getApiKey();
        ApiKey updatedApiKey = apiKeyRepository
                .findByApiKey(_apiKey)
                .orElseThrow(
                        () -> new RuntimeException("API key not found for key: " + _apiKey));

        // Publish updated apiKey to API_KEY_PUBLISHED queue with a relevant header
        // Map<String, Object> messageHeader = Map.of("message_type", apiKeyStatus,
        // "message_queue", apikeyPublishedQueue);

        ApiKeyPublishDto apiKeyToPublish = new ApiKeyPublishDto();
        apiKeyToPublish.setSystemName(updatedApiKey.getSystemName());
        apiKeyToPublish.setCode(updatedApiKey.getCode());
        apiKeyToPublish.setApiKey(updatedApiKey.getApiKey());
        apiKeyToPublish.setSystemIp(updatedApiKey.getSystemIp());
        apiKeyToPublish.setExpiryDate(updatedApiKey.getExpiryDate());

        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            String serializedApiKey = mapper.writeValueAsString(updatedApiKey);
            log.info("Updated ApiKey: {}", serializedApiKey);
        } catch (JsonProcessingException e) {
            log.error("Error serializing updatedApiKey to JSON", e);
        }

        // generate the apikey metadata
        ApiKeyMetadata apiKeyMetadata = new ApiKeyMetadata();
        apiKeyMetadata.setApiKey(createdApiKey);
        apiKeyMetadata.setRateLimit(1000);
        apiKeyMetadata.setRateLimitReset(createdApiKey.getCreatedAt());
        apiKeyMetadata.setUsageCount(0L);
        apiKeyMetadata.setCreatedIp(createdApiKey.getSystemIp());

        List<String> allowedIps = new ArrayList<>();
        allowedIps.add(createdApiKey.getSystemIp());

        try {
            apiKeyMetadataService.setAllowedIps(apiKeyMetadata, allowedIps);
        } catch (Exception e) {
            e.printStackTrace();
        }

        apiKeyMetadataRepository.save(apiKeyMetadata);

        // instantiate the response dto
        ApiKeyResponseDto responseDto = new ApiKeyResponseDto();
        BeanUtils.copyProperties(createdApiKey, responseDto);

        return responseDto;
    }

    public boolean isValidIPAddress(String ipAddress) {
        String IPV4_REGEX = "^([0-9]{1,3}\\.){3}[0-9]{1,3}$";
        String IPV6_REGEX = "([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}";

        Pattern IPV4_PATTERN = Pattern.compile(IPV4_REGEX);
        Pattern IPV6_PATTERN = Pattern.compile(IPV6_REGEX);
        if (IPV4_PATTERN.matcher(ipAddress).matches()) {
            String[] parts = ipAddress.split("\\.");
            for (String part : parts) {
                int intPart = Integer.parseInt(part);
                if (intPart < 0 || intPart > 255) {
                    return false;
                }
            }
            return true;
        } else
            return IPV6_PATTERN.matcher(ipAddress).matches();
    }

    /**
     * takes a roleDto and upcase its name and then use the name to create a joined
     * string as role
     * code
     *
     * @return RoleDto
     */
    private ApiKeyRequestDto sanitizeRequest(ApiKeyRequestDto apiKeyRequestDto) {
        String name = apiKeyRequestDto.getSystemName().toUpperCase();
        String code = String.join("_", name.split(" "));

        apiKeyRequestDto.setCode(code);
        apiKeyRequestDto.setSystemName(name);

        return apiKeyRequestDto;
    }
}
