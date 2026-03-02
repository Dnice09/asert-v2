package tz.go.mnrt.asert.modules.menugroup.rest;

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
import tz.go.mnrt.asert.modules.menugroup.dtos.MenuGroupDto;
import tz.go.mnrt.asert.modules.menugroup.service.MenuGroupService;

@RestController
@RequestMapping(Constant.API_V1 + "/menu-groups")
@RequiredArgsConstructor
@Slf4j
public class MenuGroupResource {
    final MenuGroupService menuGroupService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                menuGroupService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody MenuGroupDto menuGroupDto) {
        if (menuGroupDto.getId() != null || menuGroupDto.getUuid() != null) {
            throw new ValidationException("New menuGroup cannot contain id or uuid");
        }

        return CustomApiResponse.ok("MenuGroup create successfully", menuGroupService.save(menuGroupDto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody MenuGroupDto menuGroupDto, @PathVariable UUID uuid) {
        if (menuGroupDto.getUuid() == null || !Objects.equals(menuGroupDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "MenuGroup id must be present and equals to path id {" + uuid + "}");
        }
        menuGroupService.save(menuGroupDto);
        return CustomApiResponse.ok("MenuGroup updated successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(menuGroupService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        menuGroupService.delete(uuid);
        return CustomApiResponse.ok("MenuGroup deleted successfully");
    }
}
