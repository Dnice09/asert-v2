package tz.go.mnrt.asert.modules.adminhierarchylevel.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.role.entity.Role;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "admin_hierarchy_levels")
public class AdminHierarchyLevel extends BaseModel {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "position", nullable = false, unique = true)
    private Integer position;

    @OneToMany(mappedBy = "level")
    @JsonIgnoreProperties({ "adminHierarchyLevel", "users", "authorities" })
    @JsonIgnore
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "adminHierarchyLevel")
    @JsonIgnoreProperties({ "adminHierarchyLevel", "parent" })
    @JsonIgnore
    @Builder.Default
    private Set<AdminHierarchy> adminHierarchies = new HashSet<>();
}
