package tz.go.mnrt.asert.modules.setup.country.repository;

import java.util.Optional;
import java.util.UUID;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.country.entity.Country;

public interface CountryRepository
    extends BaseRepository<Country, Long> {

  Optional<Country> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);
}
