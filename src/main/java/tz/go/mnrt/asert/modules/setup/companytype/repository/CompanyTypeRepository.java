package tz.go.mnrt.asert.modules.setup.companytype.repository;

import java.util.Optional;
import java.util.UUID;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.setup.companytype.entity.CompanyType;

public interface CompanyTypeRepository
    extends BaseRepository<CompanyType, Long> {

  Optional<CompanyType> findByUuid(UUID uuid);

  void deleteByUuid(UUID uuid);

    Optional<CompanyType> findByCode(String code);
}
