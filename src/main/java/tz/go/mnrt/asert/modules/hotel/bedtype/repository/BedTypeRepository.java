package tz.go.mnrt.asert.modules.hotel.bedtype.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.hotel.bedtype.entity.BedType;

public interface BedTypeRepository
        extends BaseRepository<BedType, Long> {

    Page<BedType> findAll(Specification<BedType> specification, Pageable pageable);

    Optional<BedType> findByUuid(UUID uuid);

    Optional<BedType> findById(Long id);

    void deleteByUuid(UUID uuid);
}
