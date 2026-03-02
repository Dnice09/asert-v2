package tz.go.mnrt.asert.modules.hotel.roomtype.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeResponseDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;

public interface RoomTypeService {
    /**
     * Save or update a room type
     *
     * @param roomTypeDto The room type data to save
     * @return The saved room type data
     */
    RoomTypeRequestDto save(RoomTypeRequestDto roomTypeDto);

    /**
     * Find all room types with pagination and filtering
     *
     * @param page   Pagination parameters
     * @param search Search parameters
     * @return Page of room types
     */
    Page<RoomTypeResponseDto> findAll(Pageable page, Map<String, String> search);

    /**
     * Find room type by UUID
     *
     * @param uuid Room type UUID
     * @return Room type data
     */
    RoomTypeResponseDto findByUuid(UUID uuid);

    /**
     * Find room type entity by UUID
     *
     * @param uuid Room type UUID
     * @return Room type entity
     */
    RoomType findEntityByUuid(UUID uuid);

    /**
     * Delete room type by UUID
     *
     * @param uuid Room type UUID
     */
    void delete(UUID uuid);

    /**
     * Find all room types by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param page      Pagination parameters
     * @return Page of room types
     */
    Page<RoomTypeResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page);
}
