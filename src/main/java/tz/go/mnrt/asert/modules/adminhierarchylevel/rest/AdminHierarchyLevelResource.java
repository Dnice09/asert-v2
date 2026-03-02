package tz.go.mnrt.asert.modules.adminhierarchylevel.rest;

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
import tz.go.mnrt.asert.modules.adminhierarchylevel.dtos.AdminHierarchyLevelDto;
import tz.go.mnrt.asert.modules.adminhierarchylevel.services.AdminHierarchyLevelService;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;

@RestController
@RequestMapping(Constant.API_V1 + "/admin-hierarchy-levels")
@RequiredArgsConstructor
@Slf4j
public class AdminHierarchyLevelResource {
    private final AdminHierarchyLevelService levelService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                levelService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody AdminHierarchyLevelDto dto) {
        if (dto.getId() != null || dto.getUuid() != null) {
            throw new ValidationException("New adminHierarchyLevel cannot contain id or uuid");
        }

        return CustomApiResponse.created("AdminHierarchyLevel create successfully", levelService.save(dto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(
            @Valid @RequestBody AdminHierarchyLevelDto dto, @PathVariable UUID uuid) {
        if (dto.getUuid() == null || !Objects.equals(dto.getUuid(), uuid)) {
            throw new ValidationException(
                    "AdminHierarchyLevel id must be present and equals to path id {" + uuid + "}");
        }
        levelService.save(dto);
        return CustomApiResponse.ok("AdminHierarchyLevel updated successfully");
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(levelService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        levelService.delete(uuid);
        return CustomApiResponse.ok("AdminHierarchyLevel deleted successfully");
    }
}
