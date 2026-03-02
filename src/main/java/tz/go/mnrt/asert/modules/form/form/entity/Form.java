package tz.go.mnrt.asert.modules.form.form.entity;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;
import tz.go.mnrt.asert.modules.form.formsection.entity.FormSection;
import tz.go.mnrt.asert.modules.hotel.hotel.enums.PropertyType;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "forms")
public class Form extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @ElementCollection(targetClass = PropertyType.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "form_property_types",
                     joinColumns = @JoinColumn(name = "form_id"))
    @Column(name = "property_type")
    @Builder.Default
    private Set<PropertyType> propertyTypes = new HashSet<>();

    @OneToMany(mappedBy = "form", cascade = CascadeType.ALL, orphanRemoval = true)
    @BatchSize(size = 20)
    @Builder.Default
    private Set<FormSection> sections = new HashSet<>();
}
