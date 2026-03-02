package tz.go.mnrt.asert.modules.menugroup.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.beans.BeanUtils;
import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;
import tz.go.mnrt.asert.modules.menuitem.dtos.MenuItemDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MenuGroupDto implements Serializable {
    private Long id;
    private UUID uuid;
    @NotNull
    private String name;
    private String icon;
    private String state;
    private Integer sortOrder;

    private String translationLabel;

    private List<MenuItemDto> menus = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<MenuItemDto> children = new ArrayList<>();

    public MenuGroupDto(MenuGroup menuGroup) {
        BeanUtils.copyProperties(menuGroup, this);
        menus = menuGroup.getMenuItems().stream().map(MenuItemDto::new).collect(Collectors.toList());
        children = null;
    }

    public MenuGroupDto(
            Long id, String name, String icon, String state, Integer sortOrder, String translationLabel) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.state = state;
        this.sortOrder = sortOrder;
        this.translationLabel = translationLabel;
    }
}
