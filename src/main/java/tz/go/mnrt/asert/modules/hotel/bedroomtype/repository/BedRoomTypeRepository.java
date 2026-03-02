package tz.go.mnrt.asert.modules.hotel.bedroomtype.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.entity.BedRoomType;

public interface BedRoomTypeRepository
        extends BaseRepository<BedRoomType, Long> {

    Page<BedRoomType> findAll(Specification<BedRoomType> specification, Pageable pageable);

    Optional<BedRoomType> findByUuid(UUID uuid);

    Optional<BedRoomType> findById(Long id);

    void deleteByUuid(UUID uuid);
}
