package tz.go.mnrt.asert.modules.hotel.hotelmedia.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.entity.HotelMedia;

public interface HotelMediaService {
    /**
     * Save or update hotel media
     *
     * @param mediaDto The media data to save
     * @return The saved media data
     */
    HotelMediaRequestDto save(HotelMediaRequestDto mediaDto);

    /**
     * Find all hotel media with pagination and filtering
     *
     * @param page   Pagination parameters
     * @param search Search parameters
     * @return Page of hotel media
     */
    Page<HotelMediaResponseDto> findAll(Pageable page, Map<String, String> search);

    /**
     * Find hotel media by UUID
     *
     * @param uuid Media UUID
     * @return Media data
     */
    HotelMediaResponseDto findByUuid(UUID uuid);

    /**
     * Find hotel media entity by UUID
     *
     * @param uuid Media UUID
     * @return Media entity
     */
    HotelMedia findEntityByUuid(UUID uuid);

    /**
     * Delete hotel media by UUID
     *
     * @param uuid Media UUID
     */
    void delete(UUID uuid);

    /**
     * Find all media by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param page      Pagination parameters
     * @return Page of hotel media
     */
    Page<HotelMediaResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page);

    /**
     * Set a media item as the default image for a hotel
     *
     * @param hotelUuid Hotel UUID
     * @param mediaUuid Media UUID
     * @return Updated media data
     */
    HotelMediaResponseDto setAsDefaultImage(UUID hotelUuid, UUID mediaUuid);
}
