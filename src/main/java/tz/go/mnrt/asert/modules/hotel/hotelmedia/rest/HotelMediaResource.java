package tz.go.mnrt.asert.modules.hotel.hotelmedia.rest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.dtos.HotelMediaRequestDto;
import tz.go.mnrt.asert.modules.hotel.hotelmedia.services.HotelMediaService;
import tz.go.mnrt.asert.modules.hotel.hotel.services.HotelService;

@RestController
@RequestMapping(Constant.API_V1 + "/hotels/{hotelUuid}/media")
@RequiredArgsConstructor
@Slf4j
public class HotelMediaResource {

    final HotelMediaService mediaService;
    final HotelService hotelService;

    @GetMapping()
    public CustomApiResponse getAllByHotel(
            @PathVariable("hotelUuid") UUID hotelUuid,
            Pageable pagination,
            @RequestParam() Map<String, String> search) {

        log.info("Loading media for hotel UUID: {}", hotelUuid);

        // Add hotel uuid to search parameters
        search.put("hotel.uuid", hotelUuid.toString());

        return CustomApiResponse.ok(
                mediaService.findAll(
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
            @Valid @RequestBody HotelMediaRequestDto mediaDto) {

        if (mediaDto.getId() != null || mediaDto.getUuid() != null) {
            throw new ValidationException("New media cannot contain id or uuid");
        }

        // Verify hotel exists and set it in the DTO
        var hotel = hotelService.findEntityByUuid(hotelUuid);
        mediaDto.setHotelId(hotel.getId());

        return CustomApiResponse.created(
                "Media created successfully",
                mediaService.save(mediaDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid) {

        // Find the media
        var media = mediaService.findByUuid(uuid);

        // Verify the media belongs to this hotel
        if (!media.getHotelId().equals(hotelService.findByUuid(hotelUuid).getId())) {
            throw new ValidationException(
                    "Media with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        return CustomApiResponse.ok(media);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid,
            @Valid @RequestBody HotelMediaRequestDto mediaDto) {

        if (mediaDto.getUuid() == null || !Objects.equals(mediaDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "Media uuid must be present and equal to path uuid {" + uuid + "}");
        }

        // Verify hotel exists and set it in the DTO
        var hotel = hotelService.findEntityByUuid(hotelUuid);
        mediaDto.setHotelId(hotel.getId());

        // Verify the media belongs to this hotel
        var existingMedia = mediaService.findEntityByUuid(uuid);
        if (!existingMedia.getHotel().getId().equals(hotel.getId())) {
            throw new ValidationException(
                    "Media with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        return CustomApiResponse.accepted(
                "Media updated successfully",
                mediaService.save(mediaDto));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid) {

        // Verify the media belongs to this hotel
        var media = mediaService.findEntityByUuid(uuid);
        if (!media.getHotel().getId().equals(hotelService.findEntityByUuid(hotelUuid).getId())) {
            throw new ValidationException(
                    "Media with uuid {" + uuid + "} does not belong to hotel with uuid {" + hotelUuid + "}");
        }

        mediaService.delete(uuid);
        return CustomApiResponse.noContent("Media deleted successfully");
    }

    @PutMapping("/{uuid}/set-default")
    @Transactional
    public CustomApiResponse setAsDefault(
            @PathVariable("hotelUuid") UUID hotelUuid,
            @PathVariable("uuid") UUID uuid) {

        return CustomApiResponse.accepted(
                "Default image set successfully",
                mediaService.setAsDefaultImage(hotelUuid, uuid));
    }
}
