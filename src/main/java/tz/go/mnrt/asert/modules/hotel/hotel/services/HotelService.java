package tz.go.mnrt.asert.modules.hotel.hotel.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tz.go.mnrt.asert.modules.assessment.assessor.dto.AssessorResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelAssessorsAssignmentRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelListingResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotel.dtos.HotelResponseMinDto;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface HotelService {
    /**
     * Save or update a hotel
     *
     * @param hotelDto The hotel data to save
     * @return The saved hotel data
     */
    HotelRequestDto save(HotelRequestDto hotelDto);

    /**
     * Find all hotels with pagination and filtering
     *
     * @param page   Pagination parameters
     * @param search Search parameters
     * @return Page of hotels
     */
    Page<HotelResponseDto> findAll(Pageable page, Map<String, String> search);

    Page<HotelListingResponseDto> findPublicListing(Pageable page, Map<String, String> search);

    Page<HotelResponseMinDto> newAssignedHotels(Pageable page, Map<String, String> search);

    Page<HotelResponseMinDto> newUnAssignedHotels(Pageable page, Map<String, String> search);

    /**
     * Find hotel by UUID
     *
     * @param id Hotel UUID
     * @return Hotel data
     */
    HotelResponseDto findByUuid(UUID id);

    HotelResponseDto findPublicFacilityByUuid(UUID id);

    /**
     * Find hotel entity by UUID
     *
     * @param uuid Hotel UUID
     * @return Hotel entity
     */
    Hotel findEntityByUuid(UUID uuid);

    /**
     * Delete hotel by UUID
     *
     * @param uuid Hotel UUID
     */
    void delete(UUID uuid);

    List<PropertyType> getAllTypes();

    List<?> importQuery(String tin);

    void assignAssessorsToHotel(UUID hotelUuid, HotelAssessorsAssignmentRequestDto request);

    HotelResponseDto removeAssessorsFromHotel(UUID hotelUuid, List<UUID> assessorUuids);

    List<AssessorResponseDto> getHotelAssessors(UUID hotelUuid);

    HotelResponseDto requestAssessment(UUID uuid);
}
