package tz.go.mnrt.asert.modules.role.dtos;

import java.util.Map;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tz.go.mnrt.asert.modules.hotel.hotel.state.HotelState;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StateRoleDto {
    private Map<HotelState, Set<String>> stateRoles;
}
