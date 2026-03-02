package tz.go.mnrt.asert.modules.hotel.roomtype.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import tz.go.mnrt.asert.modules.core.repo.BaseRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;

@Repository
public interface RoomTypeRepository extends BaseRepository<RoomType, Long> {

    /**
     * Find room type by UUID
     *
     * @param uuid Room type UUID
     * @return Optional containing room type if found
     */
    Optional<RoomType> findByUuid(UUID uuid);

    /**
     * Find all room types by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param pageable  Pagination parameters
     * @return Page of room types
     */
    @Query("SELECT rt FROM RoomType rt WHERE rt.hotel.uuid = :hotelUuid AND rt.isDeleted = false")
    Page<RoomType> findByHotelUuid(UUID hotelUuid, Pageable pageable);

    /**
     * Soft delete a room type by UUID
     *
     * @param uuid Room type UUID
     */
    @Modifying
    @Query("UPDATE RoomType rt SET rt.isDeleted = true WHERE rt.uuid = :uuid")
    void softDelete(UUID uuid);

    List<RoomType> findByHotelId(Long id);
}
