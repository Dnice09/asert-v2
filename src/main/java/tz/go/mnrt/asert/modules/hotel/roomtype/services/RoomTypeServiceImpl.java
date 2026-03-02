package tz.go.mnrt.asert.modules.hotel.roomtype.services;

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
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeResponseDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.entity.RoomType;
import tz.go.mnrt.asert.modules.hotel.roomtype.repository.RoomTypeRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomTypeServiceImpl extends SimpleSearchService<RoomType> implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;

    @Override
    @Transactional
    public RoomTypeRequestDto save(RoomTypeRequestDto roomTypeDto) {
        log.info("Saving room type: {}", roomTypeDto);

        // Find or create room type
        RoomType roomType = new RoomType();
        if (roomTypeDto.getUuid() != null) {
            roomType = roomTypeRepository
                    .findByUuid(roomTypeDto.getUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Room type with uuid {" + roomTypeDto.getUuid() + "} not found"));
        }

        // Find hotel
        Hotel hotel = hotelRepository
                .findById(roomTypeDto.getHotelId())
                .orElseThrow(
                        () -> new ValidationException("Hotel with id {" + roomTypeDto.getHotelId() + "} not found"));

        // Copy properties from DTO to entity
        BeanUtils.copyProperties(roomTypeDto, roomType, "uuid", "amenities", "hotel");

        // Set hotel
        roomType.setHotel(hotel);

        // Handle amenities - clear existing and add new ones
        roomType.getAmenities().clear();
        if (roomTypeDto.getAmenities() != null && !roomTypeDto.getAmenities().isEmpty()) {
            roomType.getAmenities().addAll(roomTypeDto.getAmenities());
        }

        // Save room type
        roomType = roomTypeRepository.save(roomType);

        // Update DTO with generated ID
        roomTypeDto.setId(roomType.getId());

        log.info("Room type saved successfully with ID: {}", roomType.getId());
        return roomTypeDto;
    }

    @Override
    public Page<RoomTypeResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated room types with page {} and search {}", page, search);

        return roomTypeRepository
                .findAll(createSpecification(RoomType.class, search), page)
                .map(RoomTypeResponseDto::new);
    }

    @Override
    public RoomTypeResponseDto findByUuid(UUID uuid) {
        log.info("Finding room type with uuid {}", uuid);

        return roomTypeRepository
                .findByUuid(uuid)
                .map(RoomTypeResponseDto::new)
                .orElseThrow(() -> new ValidationException("Room type with uuid {" + uuid + "} not found"));
    }

    @Override
    public RoomType findEntityByUuid(UUID uuid) {
        log.info("Finding room type entity with uuid {}", uuid);

        return roomTypeRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Room type with uuid {" + uuid + "} not found"));
    }

    @Override
    @Transactional
    public void delete(UUID uuid) {
        log.info("Deleting room type with uuid {}", uuid);
        roomTypeRepository.softDelete(uuid);
    }

    @Override
    public Page<RoomTypeResponseDto> findAllByHotelUuid(UUID hotelUuid, Pageable page) {
        log.info("Finding all room types for hotel with uuid {}", hotelUuid);

        return roomTypeRepository
                .findByHotelUuid(hotelUuid, page)
                .map(RoomTypeResponseDto::new);
    }
}
