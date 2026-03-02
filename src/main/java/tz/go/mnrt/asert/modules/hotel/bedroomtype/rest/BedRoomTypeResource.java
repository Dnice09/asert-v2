package tz.go.mnrt.asert.modules.hotel.bedroomtype.rest;

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
import tz.go.mnrt.asert.modules.hotel.bedroomtype.dtos.BedRoomTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedroomtype.services.BedRoomTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/bed-room-types")
@RequiredArgsConstructor
@Slf4j
public class BedRoomTypeResource {

    final BedRoomTypeService bedRoomTypeService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                bedRoomTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody BedRoomTypeRequestDto bedTypeRequestDto) {
        if (bedTypeRequestDto.getId() != null || bedTypeRequestDto.getUuid() != null) {
            throw new ValidationException("New BedRoomType cannot contain id or uuid");
        }
        return CustomApiResponse.created("BedRoomType create successfully", bedRoomTypeService.save(bedTypeRequestDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody BedRoomTypeRequestDto bedTypeDto, @PathVariable UUID uuid) {
        if (bedTypeDto.getUuid() == null || !Objects.equals(bedTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "BedRoomType id must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.accepted("BedRoomType updated successfully", bedRoomTypeService.save(bedTypeDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(bedRoomTypeService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        bedRoomTypeService.delete(uuid);
        return CustomApiResponse.noContent("BedRoomType deleted successfully");
    }
}
