package tz.go.mnrt.asert.modules.menuitem.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import tz.go.mnrt.asert.modules.authority.entity.Authority;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.menugroup.entity.MenuGroup;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "menu_items")
public class MenuItem extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "icon")
    private String icon;

    @Column(name = "state", nullable = false, unique = true)
    private String state;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "translation_label")
    private String translationLabel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_group_id")
    @JsonIgnoreProperties({ "menuItems" })
    @JsonIgnore
    private MenuGroup menuGroup;

    @Column(name = "menu_group_id", insertable = false, updatable = false)
    private Long menuGroupId;

    @ManyToMany(cascade = { CascadeType.MERGE, CascadeType.PERSIST })
    @JoinTable(name = "menu_item_authorities", joinColumns = {
            @JoinColumn(name = "menu_item_id") }, inverseJoinColumns = { @JoinColumn(name = "authority_id") })
    @JsonIgnoreProperties({ "roles", "menuItems" })
    @JsonIgnore
    @Builder.Default
    private Set<Authority> authorities = new HashSet<>();

    public void addAuthority(Authority authority) {
        authorities.add(authority);
        authority.getMenuItems().add(this);
    }

    public void removeAuthority(Authority authority) {
        authorities.remove(authority);
        authority.getMenuItems().remove(this);
    }
}
