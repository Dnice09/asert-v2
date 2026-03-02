package tz.go.mnrt.asert.modules.hotel.hotelmedia.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;

import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;

@Repository
public interface HotelMediaRepository extends BaseRepository<HotelMedia, Long> {

    /**
     * Find hotel media by UUID
     *
     * @param uuid Media UUID
     * @return Optional containing media if found
     */
    Optional<HotelMedia> findByUuid(UUID uuid);

    /**
     * Find all media by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param pageable  Pagination parameters
     * @return Page of media
     */
    @Query("SELECT m FROM HotelMedia m WHERE m.hotel.uuid = :hotelUuid AND m.isDeleted = false")
    Page<HotelMedia> findByHotelUuid(UUID hotelUuid, Pageable pageable);

    /**
     * Find default media for a hotel
     *
     * @param hotelId Hotel ID
     * @return Optional containing default media if found
     */
    Optional<HotelMedia> findByHotelIdAndIsDefaultTrue(Long hotelId);

    /**
     * Soft delete media by UUID
     *
     * @param uuid Media UUID
     */
    @Modifying
    @Query("UPDATE HotelMedia m SET m.isDeleted = true WHERE m.uuid = :uuid")
    void softDelete(UUID uuid);
}
