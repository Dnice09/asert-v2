package tz.go.mnrt.asert.modules.systemconfiguration.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.systemconfiguration.dtos.SystemConfigurationRequestDto;
import tz.go.mnrt.asert.modules.systemconfiguration.dtos.SystemConfigurationResponseDto;

public interface SystemConfigurationService {

  SystemConfigurationRequestDto save(SystemConfigurationRequestDto systemConfigurationDto);

  Page<SystemConfigurationResponseDto> findAll(Pageable page, Map<String, String> search);

  SystemConfigurationResponseDto findByUuid(UUID id);

  void delete(UUID uuid);

  /**
   * Get the variance threshold configuration value.
   *
   * @return The current variance threshold as a Double
   */
  Double getVarianceThreshold();

  /**
   * Update the variance threshold configuration.
   *
   * @param threshold The new threshold value
   * @return The updated configuration
   */
  SystemConfigurationResponseDto updateVarianceThreshold(Double threshold);

  /**
   * Get a configuration by its key.
   *
   * @param configKey The configuration key
   * @return The configuration response
   */
  SystemConfigurationResponseDto getByKey(String configKey);

  /**
   * Update a configuration by its key.
   *
   * @param configKey The configuration key
   * @param value The new value
   * @return The updated configuration
   */
  SystemConfigurationResponseDto updateByKey(String configKey, String value);
}

