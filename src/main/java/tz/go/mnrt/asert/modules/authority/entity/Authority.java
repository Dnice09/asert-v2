package tz.go.mnrt.asert.modules.authority.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.role.entity.Role;
import tz.go.mnrt.asert.modules.menuitem.entity.MenuItem;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "authorities")
public class Authority extends BaseModel {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "resource")
    private String resource;

    @Column(name = "action")
    private String action;

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "authorities")
    @Builder.Default
    private Set<MenuItem> menuItems = new HashSet<>();
}
