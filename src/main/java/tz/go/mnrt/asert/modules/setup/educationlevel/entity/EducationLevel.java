package tz.go.mnrt.asert.modules.setup.educationlevel.entity;

import lombok.*;
import tz.go.mnrt.asert.modules.core.entities.BaseModel;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "education_levels")
public class EducationLevel extends BaseModel {
    @Column(name = "name", nullable = false)
    private String name;
}
