package tz.go.mnrt.asert.modules.hotel.roomtype.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;
import tz.go.mnrt.asert.modules.hotel.roomtype.dtos.RoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.roomtype.services.RoomTypeService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping(Constant.API_V1 + "/hotels/{hotelUuid}/room-types")
@RequiredArgsConstructor
@Slf4j
public class RoomTypeResource {

    final RoomTypeService roomTypeService;
    final HotelService hotelService;

    @GetMapping()
    public CustomApiResponse getAllByHotel(
            @PathVariable("hotelUuid") UUID hotelUuid,
            Pageable pagination,
            @RequestParam() Map<String, String> search) {

        log.info("Loading room type for hotel UUID: {}", hotelUuid);

        // Add hotel uuid to search parameters
        search.put("hotel.uuid", hotelUuid.toString());

        return CustomApiResponse.ok(
                roomTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @Valid @RequestBody RoomTypeRequestDto roomTypeDto) {

        if (roomTypeDto.getId() != null || roomTypeDto.getUuid() != null) {
            throw new ValidationException("New room type cannot contain id or uuid");
        }

        // Verify hotel exists and set it in the DTO
        var hotel = hotelService.findEntityByUuid(hotelUuid);
        roomTypeDto.setHotelId(hotel.getId());

        return CustomApiResponse.created(
                "Room type created successfully",
                roomTypeService.save(roomTypeDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid) {

        // Find the room type
        var roomType = roomTypeService.findByUuid(uuid);

        // Verify the Room type belongs to this hotel
        if (!roomType.getHotelId().equals(hotelService.findByUuid(hotelUuid).getId())) {
            throw new ValidationException(
                    "Room Type with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        return CustomApiResponse.ok(roomType);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody RoomTypeRequestDto roomTypeDto) {

        if (roomTypeDto.getUuid() == null || !Objects.equals(roomTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Room type uuid must be present and equal to path uuid {" + uuid + "}");
        }

        // Verify hotel exists and set it in the DTO
        var hotel = hotelService.findEntityByUuid(hotelUuid);
        roomTypeDto.setHotelId(hotel.getId());

        // Verify the room type belongs to this hotel
        var existingMedia = roomTypeService.findEntityByUuid(uuid);
        if (!existingMedia.getHotel().getId().equals(hotel.getId())) {
            throw new ValidationException(
                    "Room type with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        return CustomApiResponse.accepted(
                "Room type updated successfully",
                roomTypeService.save(roomTypeDto));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid) {

        // Verify the media belongs to this hotel
        var media = roomTypeService.findEntityByUuid(uuid);
        if (!media.getHotel().getId().equals(hotelService.findEntityByUuid(hotelUuid).getId())) {
            throw new ValidationException(
                    "Room type with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        roomTypeService.delete(uuid);
        return CustomApiResponse.noContent("Room type deleted successfully");
    }


}
