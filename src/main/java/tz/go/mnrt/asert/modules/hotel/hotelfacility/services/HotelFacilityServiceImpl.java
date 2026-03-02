package tz.go.mnrt.asert.modules.hotel.hotelfacility.services;

import java.util.Map;
import java.util.UUID;

import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.hotel.hotel.entity.Hotel;
import tz.go.mnrt.asert.modules.hotel.hotel.repository.HotelRepository;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.dtos.HotelFacilityResponseDto;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.entity.HotelFacility;
import tz.go.mnrt.asert.modules.hotel.hotelfacility.repository.HotelFacilityRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class HotelFacilityServiceImpl extends SimpleSearchService<HotelFacility> implements HotelFacilityService {

    private final HotelFacilityRepository hotelFacilityRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public HotelFacilityRequestDto save(HotelFacilityRequestDto facilityDto) {
        log.info("Saving hotel facility: {}", facilityDto);

        // Find or create facility
        HotelFacility facility = new HotelFacility();
        if (facilityDto.getUuid() != null) {
            facility = hotelFacilityRepository
                    .findByUuid(facilityDto.getUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Hotel facility with uuid {" + facilityDto.getUuid() + "} not found"));
        }

        // Find hotel
        Hotel hotel = hotelRepository
                .findById(facilityDto.getHotelId())
                .orElseThrow(
                        () -> new ValidationException("Hotel with id {" + facilityDto.getHotelId() + "} not found"));

        // Copy properties from DTO to entity
        BeanUtils.copyProperties(facilityDto, facility, "uuid", "hotel");

        // Set hotel
        facility.setHotel(hotel);

        // Save facility
        facility = hotelFacilityRepository.save(facility);

        // Update DTO with generated ID
        facilityDto.setId(facility.getId());

        log.info("Hotel facility saved successfully with ID: {}", facility.getId());
        return facilityDto;
    }

    @Override
    public Page<HotelFacilityResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated hotel facilities with page {} and search {}", page, search);

        return hotelFacilityRepository
                .findAll(createSpecification(HotelFacility.class, search), page)
                .map(HotelFacilityResponseDto::new);
    }

    @Override
    public HotelFacilityResponseDto findByUuid(UUID uuid) {
        log.info("Finding hotel facility with uuid {}", uuid);

        return hotelFacilityRepository
                .findByUuid(uuid)
                .map(HotelFacilityResponseDto::new)
                .orElseThrow(() -> new ValidationException("Hotel facility with uuid {" + uuid + "} not found"));
    }

    @Override
    public HotelFacility findEntityByUuid(UUID uuid) {
        log.info("Finding hotel facility entity with uuid {}", uuid);

        return hotelFacilityRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Hotel facility with uuid {" + uuid + "} not found"));
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("Deleting hotel facility with uuid {}", uuid);
        hotelFacilityRepository.softDelete(uuid);
    }

    @Override
    public Page<HotelFacilityResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page) {
        log.info("Finding all facilities for hotel with uuid {}", hotelUuid);

        return hotelFacilityRepository
                .findByHotelUuid(hotelUuid, page)
                .map(HotelFacilityResponseDto::new);
    }
}
