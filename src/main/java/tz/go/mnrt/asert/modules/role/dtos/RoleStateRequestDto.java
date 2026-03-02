package tz.go.mnrt.asert.modules.role.dtos;

import java.util.Set;
import java.util.UUID;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;
import tz.go.mnrt.asert.modules.role.entity.Role;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoleStateRequestDto {
    private Long id;
    private UUID uuid;

    private Long roleId;
    private Set<HotelState> states;

    private String roleName;

    public RoleStateRequestDto(Role entity) {
        entity.toDao(this);
    }

    public RoleStateRequestDto withStates(Role role) {
        BeanUtils.copyProperties(role, this);
        ;
        setRoleName(role.getName());
        setStates(role.getStates());
        return this;
    }
}
