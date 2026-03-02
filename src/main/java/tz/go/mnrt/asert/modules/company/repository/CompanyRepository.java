package tz.go.mnrt.asert.modules.company.repository;

import java.util.Optional;
import java.util.UUID;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.company.entity.Company;

public interface CompanyRepository
        extends BaseRepository<Company, Long> {

    Optional<Company> findByUuid(UUID uuid);

    Optional<Company> findById(Long id);

    void deleteByUuid(UUID uuid);
}
