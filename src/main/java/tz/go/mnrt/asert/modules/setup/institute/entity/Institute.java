package tz.go.mnrt.asert.modules.setup.institute.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.adminhierarchy.entity.AdminHierarchy;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "institutes")
public class Institute extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "country_id", nullable = false, insertable = false, updatable = false)
    private AdminHierarchy country;

    @Column(name = "country_id", nullable = false)
    private Long countryId;
}
