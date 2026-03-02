package tz.go.mnrt.asert.modules.role.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleAuthoritiesDto {

    private List<Long> authorityIds = new ArrayList<>();
}
