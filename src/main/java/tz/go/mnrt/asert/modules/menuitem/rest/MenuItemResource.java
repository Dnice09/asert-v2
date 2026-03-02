package tz.go.mnrt.asert.modules.menuitem.rest;

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
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemAuthorityDto;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto;
import tz.go.mnrt.asert.modules.menuitem.service.MenuItemService;

@RestController
@RequestMapping(Constant.API_V1 + "/menu-items")
@RequiredArgsConstructor
@Slf4j
public class MenuItemResource {
    final MenuItemService menuItemService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                menuItemService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody MenuItemDto menuItemDto) {
        if (menuItemDto.getId() != null || menuItemDto.getUuid() != null) {
            throw new ValidationException("New menuItem cannot contain id or uuid");
        }
        return CustomApiResponse.created("MenuItem created successfully",
                menuItemService.save(menuItemDto));
    }

    @PostMapping("/authorities")
    @Transactional
    public CustomApiResponse assignPermission(@Valid @RequestBody MenuItemAuthorityDto dto) {
        MenuItemDto result = menuItemService.saveAuthorities(dto);
        return CustomApiResponse.created("Menu item authorities successfully", result);
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody MenuItemDto menuItemDto, @PathVariable UUID uuid) {
        if (menuItemDto.getUuid() == null || !Objects.equals(menuItemDto.getUuid(), uuid)) {
            throw new ValidationException(
                    "MenuItem id must be present and equals to path id {" + uuid + "}");
        }
        return CustomApiResponse.accepted("MenuItem updated successfully",
                menuItemService.save(menuItemDto));
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(menuItemService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        menuItemService.delete(uuid);
        return CustomApiResponse.noContent("MenuItem deleted successfully");
    }
}
