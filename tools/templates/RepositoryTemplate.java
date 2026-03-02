package tz.go.mnrt.asert.modules.#package_name.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.#package_name.entity.#module_name;

public interface #module_nameRepository
    extends BaseRepository<#module_name, Long> {

  Optional<#module_name> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
