package tz.go.mnrt.asert.modules.assessment.assessor.repository;

import tz.go.mnrt.asert.modules.assessment.assessor.entity.AssessorHotel;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import java.util.Optional;
import java.util.UUID;

public interface AssessorHotelRepository extends BaseRepository<AssessorHotel, Long> {
    void deleteByUuid(UUID uuid);

    Optional<AssessorHotel> findByAssessorIdAndHotelId(Long id, Long hotelId);
}
