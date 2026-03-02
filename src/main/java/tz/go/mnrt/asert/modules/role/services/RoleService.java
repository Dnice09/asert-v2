package tz.go.mnrt.asert.modules.role.services;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.role.dtos.RoleAuthoritiesDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleRequestDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleResponseDto;
import tz.go.mnrt.asert.modules.role.dtos.RoleStateRequestDto;
import tz.go.mnrt.asert.modules.role.dtos.StateRoleDto;
import tz.go.mnrt.asert.modules.role.entity.Role;

@Service
public interface RoleService {
    List<Role> findAll();

    RoleResponseDto save(RoleRequestDto roleRequestDto);

    Page<RoleResponseDto> findAll(Pageable page, Map<String, String> search);

    RoleResponseDto findByUuid(UUID uuid);

    void assignAuthorities(UUID uuid, RoleAuthoritiesDto dto);

    void delete(UUID uuid);

    Set<RoleResponseDto> byUserPosition();

    List<RoleStateRequestDto> getAllRoleStates();

    List<HotelState> getAllStates();

    StateRoleDto getStateRolesMapping();

    Set<String> getRolesByState(HotelState state);
}
