package tz.go.mnrt.asert.modules.menuitem.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MenuItemAuthorityDto {

    @NotNull
    private UUID menuItemUuid;

    @NotNull
    private List<Long> permissionIds = new ArrayList<>();
}
