package tz.go.mnrt.asert.modules.adminhierarchy.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import tz.go.mnrt.asert.modules.adminhierarchylevel.entity.AdminHierarchyLevel;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Table(name = "admin_hierarchies")
public class AdminHierarchy extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "iso_code", nullable = true)
    private String isoCode;

    @Column(name = "path")
    private String path;

    @Column(name = "admin_hierarchy_level_id", insertable = false, updatable = false)
    private Long levelId;

    @Column(name = "parent_id", insertable = false, updatable = false)
    private Long parentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonIgnoreProperties({ "parent", "level", "users", "children" })
    @JsonIgnore
    private AdminHierarchy parent;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_hierarchy_level_id")
    @JsonIgnoreProperties({ "roles", "roles", "adminHierarchy" })
    @JsonIgnore
    private AdminHierarchyLevel adminHierarchyLevel;

    @OneToMany(mappedBy = "parent")
    @JsonIgnoreProperties({ "children", "parent", "adminHierarchyLevel" })
    @JsonIgnore
    private Set<AdminHierarchy> children;
}
