package tz.go.mnrt.asert.modules.role.rest;

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
import tz.go.mnrt.asert.constants.Constant;
import tz.go.mnrt.asert.modules.core.rest.response.CustomApiResponse;
import tz.go.mnrt.asert.modules.role.dtos.RoleAuthoritiesDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.role.services.RoleService;

@RestController
@RequestMapping(Constant.API_V1 + "/roles")
@RequiredArgsConstructor
public class RoleResource {

    final RoleService roleService;

    @GetMapping()
    public CustomApiResponse get(Pageable pagination, @RequestParam() Map<String, String> search) {

        return CustomApiResponse.ok(
                roleService.findAll(
                        PageRequest.of(
                                pagination.getPageNumber(),
                                pagination.getPageSize(),
                                pagination.getSortOr(Sort.by("id").descending())),
                        search));
    }

    @PostMapping
    @Transactional
    public CustomApiResponse create(@Valid @RequestBody RoleRequestDto roleRequestDto) {
        if (roleRequestDto.getId() != null || roleRequestDto.getUuid() != null) {
            throw new ValidationException("New role cannot contain id or uuid");
        }

        RoleRequestDto dto = sanitizeRole(roleRequestDto);
        return CustomApiResponse.created("Role create successfully", roleService.save(dto));
    }

    @PutMapping("/{uuid}")
    @Transactional
    public CustomApiResponse update(@Valid @RequestBody RoleRequestDto roleRequestDto, @PathVariable UUID uuid) {
        if (roleRequestDto.getUuid() == null || !Objects.equals(roleRequestDto.getUuid(), uuid)) {
            throw new ValidationException("Role id must be present and equals to path id {" + uuid + "}");
        }

        RoleRequestDto dto = sanitizeRole(roleRequestDto);
        return CustomApiResponse.accepted("Role Updated successfully", roleService.save(dto));
    }

    @PostMapping("/{uuid}/assign-authorities")
    @Transactional
    public CustomApiResponse assignAuthorities(
            @Valid @RequestBody RoleAuthoritiesDto dto, @PathVariable UUID uuid) {
        roleService.assignAuthorities(uuid, dto);
        return CustomApiResponse.ok("Role updated successfully");
    }

    @GetMapping("/by-user-position")
    public CustomApiResponse findByUsePosition() {
        return CustomApiResponse.ok(roleService.byUserPosition());
    }

    @GetMapping("/get-states")
    public CustomApiResponse getStates() {
        return CustomApiResponse.ok(roleService.getAllStates());
    }

    @GetMapping("/get-state-roles-mapping")
    public CustomApiResponse getStateRolesMapping() {
        return CustomApiResponse.ok(roleService.getStateRolesMapping());
    }

    @GetMapping("/{uuid}")
    public CustomApiResponse findById(@PathVariable("uuid") UUID uuid) {
        return CustomApiResponse.ok(roleService.findByUuid(uuid));
    }

    @DeleteMapping("/{uuid}")
    @Transactional
    public CustomApiResponse delete(@PathVariable(value = "uuid") UUID uuid) {
        roleService.delete(uuid);
        return CustomApiResponse.noContent("Role deleted successfully");
    }

    /**
     * takes a roleDto and upcase its name and then use the name to create a joined
     * string as role
     * code
     *
     * @return RoleDto
     */
    private RoleRequestDto sanitizeRole(RoleRequestDto roleRequestDto) {
        String name = roleRequestDto.getName().toUpperCase();
        String code = String.join("_", name.split(" "));

        roleRequestDto.setCode(code);
        roleRequestDto.setName(name);

        return roleRequestDto;
    }
}
