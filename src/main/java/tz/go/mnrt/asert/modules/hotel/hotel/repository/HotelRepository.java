package tz.go.mnrt.asert.modules.hotel.hotel.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HotelRepository
    extends BaseRepository<Hotel, Long> {

    Optional<Hotel> findByUuid(UUID uuid);

    void deleteByUuid(UUID uuid);

    List<Hotel> findByPropertyType(PropertyType propertyType);

    /**
     * Find all hotels where the given assessor is assigned.
     * Uses the hotel_assessors join table to find hotels for a specific assessor.
     *
     * @param assessorId The ID of the assessor
     * @return List of hotels assigned to this assessor
     */
    @Query("SELECT DISTINCT h FROM Hotel h " +
           "JOIN h.assessors a " +
           "WHERE a.id = :assessorId " +
           "AND h.isDeleted = false")
    List<Hotel> findByAssessorId(@Param("assessorId") Long assessorId);
}
