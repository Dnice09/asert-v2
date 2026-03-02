package tz.go.mnrt.asert.modules.setup.attachmenttype.rest;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.ValidationException;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import tz.go.mnrt.asert.modules.setup.attachmenttype.dtos.AttachmentTypeDto;
import tz.go.mnrt.asert.modules.setup.attachmenttype.services.AttachmentTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/attachment-types")
@RequiredArgsConstructor
@Slf4j
public class AttachmentTypeResource {

    final AttachmentTypeService attachmentTypeService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                attachmentTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody AttachmentTypeDto attachmentTypeDto) {
        if (attachmentTypeDto.getId() != null || attachmentTypeDto.getUuid() != null) {
            throw new ValidationException("New AttachmentType cannot contain id or uuid");
        }
        log.info("AttachmentType create successfully");
        return CustomApiResponse.created("AttachmentType create successfully",
                attachmentTypeService.save(attachmentTypeDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody AttachmentTypeDto attachmentTypeDto, @PathVariable UUID uuid) {
        if (attachmentTypeDto.getUuid() == null || !Objects.equals(attachmentTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "AttachmentType id must be present and equals to path id {" + uuid + "}");
        }

        attachmentTypeService.save(attachmentTypeDto);
        return CustomApiResponse.ok("AttachmentType updated successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(attachmentTypeService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        attachmentTypeService.delete(uuid);
        return CustomApiResponse.noContent("AttachmentType deleted successfully");
    }
}
