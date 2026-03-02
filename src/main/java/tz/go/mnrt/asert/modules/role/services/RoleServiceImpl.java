package tz.go.mnrt.asert.modules.role.services;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.validation.ValidationException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import tz.go.mnrt.asert.modules.adminhierarchy.repository.AdminHierarchyRepository;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.core.services.SimpleSearchService;
import tz.go.mnrt.asert.modules.role.dtos.RoleAuthoritiesDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleResponseDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleStateRequestDto;
import tz.go.mnrt.asert.modules.role.dtos.StateRoleDto;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.role.repository.RoleRepository;
import tz.go.mnrt.asert.modules.user.dtos.LoggedInUserDto;
import tz.go.mnrt.asert.modules.user.service.UserService;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl extends SimpleSearchService<Role> implements RoleService {

    private final RoleRepository roleRepository;

    private final EntityManager em;

    @Lazy
    @Autowired
    private UserService userService;

    private final AdminHierarchyRepository adminHierarchyRepository;

    @Override
    public RoleResponseDto save(RoleRequestDto roleRequestDto) {
        Role role;
        if (roleRequestDto.getUuid() != null) {
            role = roleRepository.findByUuid(roleRequestDto.getUuid())
                    .orElseThrow(() -> new ValidationException(
                            "Role with uuid {" + roleRequestDto.getUuid() + "} not found"));

            // Clear existing states
            role.getStates().clear();
        } else {
            role = new Role();
        }

        BeanUtils.copyProperties(roleRequestDto, role, "uuid", "states", "authorities");

        // Set new states
        for (HotelState state : roleRequestDto.getStates()) {
            role.addState(state);
        }

        // Handle the levelId if provided
        if (roleRequestDto.getLevelId() != null) {
            role.setLevel(em.getReference(AdminHierarchyLevel.class, roleRequestDto.getLevelId()));
        }

        role = roleRepository.save(role);

        return new RoleResponseDto(role);
    }

    @Override
    public Page<RoleResponseDto> findAll(Pageable page, Map<String, String> search) {
        log.info("Loading paginated roles with page {} and search {} ", page, search);
        return roleRepository.findAll(createSpecification(Role.class, search), page)
                .map(r -> new RoleResponseDto().withStates(r));
    }

    @Override
    public RoleResponseDto findByUuid(UUID uuid) {
        log.info("finding role with uuid {} ", uuid);
        return roleRepository
                .findByUuid(uuid)
                .map(r -> new RoleResponseDto(r).withAuthorities(r.getAuthorities()))
                .orElseThrow(() -> new ValidationException("Role with uuid {" + uuid + "} not found"));
    }

    @Override
    public void assignAuthorities(UUID uuid, RoleAuthoritiesDto dto) {
        Role role = roleRepository
                .findByUuid(uuid)
                .orElseThrow(() -> new ValidationException("Role with uuid {" + uuid + "} not found"));
        role.setAuthorities(new HashSet<>());
        for (Long authorityId : dto.getAuthorityIds()) {
            role.addAuthority(em.getReference(Authority.class, authorityId));
        }
        roleRepository.save(role);
    }

    @Override
    public void delete(UUID uuid) {
        log.info("deleting role with uuid {} ", uuid);
        roleRepository.deleteByUuid(uuid);
    }

    @Override
    public Set<RoleResponseDto> byUserPosition() {
        LoggedInUserDto userDto = userService.loggedIn()
                .orElseThrow(() -> new ValidationException("User not logged in"));
        if (userDto.getAdminHierarchyId() == null) {
            return new HashSet<>();
        }
        Integer userPosition = adminHierarchyRepository.findAdminPosition(userDto.getAdminHierarchyId());

        return roleRepository.findByUserPosition(userPosition).stream()
                .map(RoleResponseDto::new)
                .collect(Collectors.toSet());
    }

    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<HotelState> getAllStates() {
        return Arrays.asList(HotelState.values());
    }

    @Override
    public List<RoleStateRequestDto> getAllRoleStates() {
        return roleRepository.findAll()
                .stream()
                .map(role -> new RoleStateRequestDto().withStates(role))
                .collect(Collectors.toList());
    }

    @Override
    public StateRoleDto getStateRolesMapping() {
        List<Object[]> results = roleRepository.findRoleStates();
        Map<HotelState, Set<String>> stateRolesMap = new HashMap<>();

        for (Object[] result : results) {
            String roleName = (String) result[0];
            HotelState state = (HotelState) result[1];

            stateRolesMap.computeIfAbsent(state, k -> new HashSet<>()).add(roleName);
        }

        return new StateRoleDto(stateRolesMap);
    }

    @Override
    public Set<String> getRolesByState(tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState state) {
        StateRoleDto approvalRoleMap = getStateRolesMapping();
        return approvalRoleMap.getStateRoles().getOrDefault(state, new HashSet<>());
    }

}
