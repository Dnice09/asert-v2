package tz.go.mnrt.asert.modules.hotel.bedtype.rest;

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
import tz.go.mnrt.asert.modules.hotel.bedtype.dtos.BedTypeRequestDto;
import tz.go.mnrt.asert.modules.hotel.bedtype.services.BedTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/bed-types")
@RequiredArgsConstructor
@Slf4j
public class BedTypeResource {

    final BedTypeService bedTypeService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                bedTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody BedTypeRequestDto bedTypeRequestDto) {
        if (bedTypeRequestDto.getId() != null || bedTypeRequestDto.getUuid() != null) {
            throw new ValidationException("New BedType cannot contain id or uuid");
        }
        return CustomApiResponse.created("BedType create successfully", bedTypeService.save(bedTypeRequestDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody BedTypeRequestDto bedTypeDto, @PathVariable UUID uuid) {
        if (bedTypeDto.getUuid() == null || !Objects.equals(bedTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "BedType id must be present and equals to path id {" + uuid + "}");
        }

        return CustomApiResponse.accepted("BedType updated successfully", bedTypeService.save(bedTypeDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(bedTypeService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        bedTypeService.delete(uuid);
        return CustomApiResponse.noContent("BedType deleted successfully");
    }
}
