package tz.go.mnrt.asert.modules.hotel.hotelfacility.services;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;

public interface HotelFacilityService {
    /**
     * Save or update a hotel facility
     *
     * @param facilityDto The facility data to save
     * @return The saved facility data
     */
    HotelFacilityRequestDto save(HotelFacilityRequestDto facilityDto);

    /**
     * Find all hotel facilities with pagination and filtering
     *
     * @param page   Pagination parameters
     * @param search Search parameters
     * @return Page of hotel facilities
     */
    Page<HotelFacilityResponseDto> findAll(Pageable page, Map<String, String> search);

    /**
     * Find hotel facility by UUID
     *
     * @param uuid Facility UUID
     * @return Facility data
     */
    HotelFacilityResponseDto findByUuid(UUID uuid);

    /**
     * Find hotel facility entity by UUID
     *
     * @param uuid Facility UUID
     * @return Facility entity
     */
    HotelFacility findEntityByUuid(UUID uuid);

    /**
     * Delete hotel facility by UUID
     *
     * @param uuid Facility UUID
     */
    void delete(UUID uuid);

    /**
     * Find all facilities by hotel UUID
     *
     * @param hotelUuid Hotel UUID
     * @param page      Pagination parameters
     * @return Page of hotel facilities
     */
    Page<HotelFacilityResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page);
}
