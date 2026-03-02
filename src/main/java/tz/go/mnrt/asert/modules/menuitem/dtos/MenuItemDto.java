package tz.go.mnrt.asert.modules.menuitem.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import org.springframework.beans.BeanUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tz.go.mnrt.asert.modules.authority.dtos.AuthorityDto;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(value = { "authorities" }, allowGetters = true)
public class MenuItemDto implements Serializable {
    private Long id;
    private UUID uuid;

    @NotNull
    private String name;

    private String icon;

    @NotNull
    private String state;

    private Integer sortOrder;
    private Long menuGroupId;
    private String menuGroupName;
    private String translationLabel;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Long> authorityIds = new ArrayList<>();

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<AuthorityDto> permissions = new ArrayList<>();

    public MenuItemDto(MenuItem menuItem) {
        BeanUtils.copyProperties(menuItem, this);
        if (menuItem.getMenuGroup() != null) {
            menuGroupId = menuItem.getMenuGroup().getId();
            menuGroupName = menuItem.getMenuGroup().getName();
        }
        permissions = null;
        authorityIds = null;
    }

    public MenuItemDto(
            Long id, String name, String icon, String state, Integer sortOrder, String translationLabel) {
        this.id = id;
        this.name = name;
        this.icon = icon;
        this.state = state;
        this.sortOrder = sortOrder;
        this.permissions = null;
        this.authorityIds = null;
        this.translationLabel = translationLabel;
    }

    public MenuItemDto withPermissions(MenuItem menuItem) {
        Set<Authority> authorities = menuItem.getAuthorities();
        BeanUtils.copyProperties(menuItem, this);
        this.setPermissions(authorities.stream().map(AuthorityDto::new).collect(Collectors.toList()));
        this.setAuthorityIds(authorities.stream().map(Authority::getId).collect(Collectors.toList()));
        return this;
    }
}
