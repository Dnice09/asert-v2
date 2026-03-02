package tz.go.mnrt.asert.modules.setup.notificationtype.rest;

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
import tz.go.mnrt.asert.modules.setup.notificationtype.dtos.NotificationTypeDto;
import tz.go.mnrt.asert.modules.setup.notificationtype.services.NotificationTypeService;

@RestController
@RequestMapping(Constant.API_V1 + "/notification-types")
@RequiredArgsConstructor
@Slf4j
public class NotificationTypeResource {

    final NotificationTypeService notificationTypeService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                notificationTypeService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody NotificationTypeDto notificationTypeDto) {
        if (notificationTypeDto.getId() != null || notificationTypeDto.getUuid() != null) {
            throw new ValidationException("New NotificationType cannot contain id or uuid");
        }

        return CustomApiResponse.created("NotificationType create successfully",
                notificationTypeService.save(notificationTypeDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody NotificationTypeDto notificationTypeDto, @PathVariable UUID uuid) {
        if (notificationTypeDto.getUuid() == null || !Objects.equals(notificationTypeDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "NotificationType id must be present and equals to path id {" + uuid + "}");
        }

        ;
        log.info("NotificationType updated successfully");
        return CustomApiResponse.accepted("NotificationType updated successfully",
                notificationTypeService.save(notificationTypeDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(notificationTypeService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        notificationTypeService.delete(uuid);
        return CustomApiResponse.noContent("NotificationType deleted successfully");
    }
}
