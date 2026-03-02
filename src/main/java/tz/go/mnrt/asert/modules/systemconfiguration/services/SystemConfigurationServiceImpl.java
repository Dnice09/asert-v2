package tz.go.mnrt.asert.modules.systemconfiguration.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.systemconfiguration.dtos.SystemConfigurationRequestDto;
import tz.go.mnrt.asert.modules.systemconfiguration.dtos.SystemConfigurationResponseDto;
import tz.go.mnrt.asert.modules.systemconfiguration.entity.SystemConfiguration;
import tz.go.mnrt.asert.modules.systemconfiguration.repository.SystemConfigurationRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class SystemConfigurationServiceImpl extends SimpleSearchService<SystemConfiguration> implements SystemConfigurationService {
  private final SystemConfigurationRepository systemConfigurationRepository;

  @Override
  public SystemConfigurationRequestDto save(SystemConfigurationRequestDto systemConfigurationRequestDto) {
    SystemConfiguration systemConfiguration = new SystemConfiguration();
    if (systemConfigurationRequestDto.getUuid() != null) {
      systemConfiguration =
          systemConfigurationRepository
              .findByUuid(systemConfigurationRequestDto.getUuid())
              .orElseThrow(
                  () ->
                      new ValidationException(
                          "SystemConfiguration with uuid {" + systemConfigurationRequestDto.getUuid() + "} not found"));
    }
    BeanUtils.copyProperties(systemConfigurationRequestDto, systemConfiguration, "uuid");
    assert (systemConfiguration.getUuid() != null);
    systemConfiguration = systemConfigurationRepository.save(systemConfiguration);
    systemConfigurationRequestDto.setId(systemConfiguration.getId());
    return systemConfigurationRequestDto;
  }

  @Override
  public Page<SystemConfigurationResponseDto> findAll(Pageable page, Map<String, String> search) {
    log.info("Loading paginated SystemConfigurations with page {} and search {} ", page, search);
    return systemConfigurationRepository
        .findAll(createSpecification(SystemConfiguration.class, search), page)
        .map(SystemConfigurationResponseDto::new);
  }

  @Override
  public SystemConfigurationResponseDto findByUuid(UUID uuid) {
    log.info("finding role with uuid {} ", uuid);
    return systemConfigurationRepository
        .findByUuid(uuid)
        .map(SystemConfigurationResponseDto::new)
        .orElseThrow(() -> new ValidationException("SystemConfiguration with uuid {" + uuid + "} not found"));
  }

  @Override
  public void delete(UUID uuid) {
    log.info("deleting SystemConfiguration with uuid {} ", uuid);
    systemConfigurationRepository.softDelete(uuid);
  }

  @Override
  public Double getVarianceThreshold() {
    log.info("Fetching variance threshold configuration");
    SystemConfiguration config = systemConfigurationRepository
        .findByConfigKey("variance.threshold")
        .orElseThrow(() -> new ValidationException("Variance threshold configuration not found"));

    try {
      return Double.parseDouble(config.getConfigValue());
    } catch (NumberFormatException e) {
      log.error("Invalid variance threshold value: {}", config.getConfigValue());
      throw new ValidationException("Invalid variance threshold value: " + config.getConfigValue());
    }
  }

  @Override
  public SystemConfigurationResponseDto updateVarianceThreshold(Double threshold) {
    log.info("Updating variance threshold to: {}", threshold);
    SystemConfiguration config = systemConfigurationRepository
        .findByConfigKey("variance.threshold")
        .orElseThrow(() -> new ValidationException("Variance threshold configuration not found"));

    config.setConfigValue(threshold.toString());
    systemConfigurationRepository.save(config);

    return new SystemConfigurationResponseDto(config);
  }

  @Override
  public SystemConfigurationResponseDto getByKey(String configKey) {
    log.info("Fetching configuration with key: {}", configKey);
    return systemConfigurationRepository
        .findByConfigKey(configKey)
        .map(SystemConfigurationResponseDto::new)
        .orElseThrow(() -> new ValidationException("Configuration with key {" + configKey + "} not found"));
  }

  @Override
  public SystemConfigurationResponseDto updateByKey(String configKey, String value) {
    log.info("Updating configuration {} with value: {}", configKey, value);
    SystemConfiguration config = systemConfigurationRepository
        .findByConfigKey(configKey)
        .orElseThrow(() -> new ValidationException("Configuration with key {" + configKey + "} not found"));

    config.setConfigValue(value);
    systemConfigurationRepository.save(config);

    return new SystemConfigurationResponseDto(config);
  }
}
