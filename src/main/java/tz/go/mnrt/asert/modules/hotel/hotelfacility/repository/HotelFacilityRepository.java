package tz.go.mnrt.asert.modules.hotel.hotelfacility.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;

@Repository
public interface HotelFacilityRepository
        extends BaseRepository<HotelFacility, Long> {

    /**
     * Find hotel facility by UUID
     *
     * @param uuid Facility UUID
     * @return Optional containing facility if found
     */
    Optional<HotelFacility> findByUuid(UUID uuid);

    /**
     * Find all facilities by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param pageable  Pagination parameters
     * @return Page of facilities
     */
    @Query("SELECT f FROM HotelFacility f WHERE f.hotel.uuid = :hotelUuid AND f.isDeleted = false")
    Page<HotelFacility> findByHotelUuid(UUID hotelUuid, Pageable pageable);

    /**
     * Find all non-deleted facilities by hotel ID
     *
     * @param hotelId Hotel ID
     * @return Set of facilities
     */
    @Query("SELECT f FROM HotelFacility f WHERE f.hotel.id = :hotelId AND f.isDeleted = false")
    List<HotelFacility> findByHotelId(Long hotelId);
}
