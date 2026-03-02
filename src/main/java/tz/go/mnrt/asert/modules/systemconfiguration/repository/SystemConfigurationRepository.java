package tz.go.mnrt.asert.modules.systemconfiguration.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.systemconfiguration.entity.SystemConfiguration;

public interface SystemConfigurationRepository
    extends BaseRepository<SystemConfiguration, Long> {

  Optional<SystemConfiguration> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

  /**
   * Find system configuration by config key.
   * Uses index on config_key for optimal performance.
   */
  @Query("SELECT sc FROM SystemConfiguration sc " +
         "WHERE sc.configKey = :configKey " +
         "AND sc.isDeleted = false")
  Optional<SystemConfiguration> findByConfigKey(@Param("configKey") String configKey);
}
