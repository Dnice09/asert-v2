package tz.go.mnrt.asert.modules.role.dtos;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import tz.go.mnrt.asert.modules.role.entity.Role;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RoleStateDto {
    private Long id;
    private UUID uuid;

    private Long roleId;

    public RoleStateDto(Role entity) {
    }
}
